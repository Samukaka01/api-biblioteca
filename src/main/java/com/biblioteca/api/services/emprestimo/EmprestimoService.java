package com.biblioteca.api.services.emprestimo;

import com.biblioteca.api.dto.requests.EmprestimoRequest;
import com.biblioteca.api.dto.responses.EmprestimoResponse;
import com.biblioteca.api.exceptions.ResourceNotFoundException;
import com.biblioteca.api.models.emprestimo.EmprestimoModel;
import com.biblioteca.api.models.emprestimo.StatusEmprestimo;
import com.biblioteca.api.models.leitor.LeitorModel;
import com.biblioteca.api.models.livro.LivroModel;
import com.biblioteca.api.repositorys.emprestimo.EmprestimoRepository;
import com.biblioteca.api.repositorys.leitor.LeitorRepository;
import com.biblioteca.api.repositorys.livro.LivroRepository;
import com.biblioteca.api.services.emprestimo.strategy.PoliticaLeitorResolver;
import com.biblioteca.api.services.emprestimo.strategy.PoliticaLeitorStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LeitorRepository leitorRepository;
    private final LivroRepository livroRepository;
    private final PoliticaLeitorResolver politicaLeitorResolver;
    private final Clock clock;
    private final Object lockEmprestimo = new Object();

    public EmprestimoService(
            EmprestimoRepository emprestimoRepository,
            LeitorRepository leitorRepository,
            LivroRepository livroRepository,
            PoliticaLeitorResolver politicaLeitorResolver,
            Clock clock) {
        this.emprestimoRepository = emprestimoRepository;
        this.leitorRepository = leitorRepository;
        this.livroRepository = livroRepository;
        this.politicaLeitorResolver = politicaLeitorResolver;
        this.clock = clock;
    }

    public List<EmprestimoResponse> listar(Long leitorId, Boolean ativos) {
        return emprestimoRepository.findAll().stream()
                .filter(e -> leitorId == null || e.getLeitorId().equals(leitorId))
                .filter(e -> {
                    if (ativos == null) {
                        return true;
                    }
                    return ativos
                            ? e.getStatus() == StatusEmprestimo.ATIVO
                            : e.getStatus() == StatusEmprestimo.DEVOLVIDO;
                })
                .map(this::toResponse)
                .toList();
    }

    public EmprestimoResponse buscarPorId(Long id) {
        EmprestimoModel emprestimo = buscarEmprestimoOuFalhar(id);
        return toResponse(emprestimo);
    }

    public EmprestimoResponse realizarEmprestimo(EmprestimoRequest request) {
        synchronized (lockEmprestimo) {
            LeitorModel leitor = buscarLeitorOuFalhar(request.leitorId());
            LivroModel livro = buscarLivroOuFalhar(request.livroId());

            long emprestimosAtivosLivro = emprestimoRepository.contarAtivosPorLivroId(livro.getId());
            long disponiveis = livro.getQuantidadeTotal() - emprestimosAtivosLivro;
            if (disponiveis <= 0) {
                throw new IllegalStateException("Não há exemplares disponíveis para o livro ID " + livro.getId() + ".");
            }

            LocalDate hoje = LocalDate.now(clock);
            boolean possuiAtraso = emprestimoRepository.findAll().stream()
                    .anyMatch(e -> e.getLeitorId().equals(leitor.getId())
                            && e.getStatus() == StatusEmprestimo.ATIVO
                            && e.getDataPrevistaDevolucao().isBefore(hoje));
            if (possuiAtraso) {
                throw new IllegalStateException("Leitor possui empréstimo em atraso e não pode realizar novos empréstimos.");
            }

            PoliticaLeitorStrategy politica = politicaLeitorResolver.resolver(leitor.getTipo());
            long ativosLeitor = emprestimoRepository.contarAtivosPorLeitorId(leitor.getId());
            if (ativosLeitor >= politica.limiteEmprestimosAtivos()) {
                throw new IllegalStateException(
                        "Leitor do tipo " + leitor.getTipo() + " atingiu o limite de " + politica.limiteEmprestimosAtivos() + " empréstimos ativos."
                );
            }

            EmprestimoModel novoEmprestimo = new EmprestimoModel(
                    null,
                    leitor.getId(),
                    livro.getId(),
                    hoje,
                    hoje.plusDays(politica.prazoEmDias()),
                    null,
                    BigDecimal.ZERO,
                    StatusEmprestimo.ATIVO
            );

            EmprestimoModel salvo = emprestimoRepository.save(novoEmprestimo);
            return EmprestimoResponse.de(salvo, livro, leitor);
        }
    }

    public EmprestimoResponse registrarDevolucao(Long id) {
        synchronized (lockEmprestimo) {
            EmprestimoModel emprestimo = buscarEmprestimoOuFalhar(id);

            if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
                throw new IllegalStateException("Empréstimo com ID " + id + " já foi devolvido.");
            }

            LocalDate hoje = LocalDate.now(clock);
            BigDecimal multa = BigDecimal.ZERO;

            LeitorModel leitor = buscarLeitorOuFalhar(emprestimo.getLeitorId());
            LivroModel livro = buscarLivroOuFalhar(emprestimo.getLivroId());

            if (hoje.isAfter(emprestimo.getDataPrevistaDevolucao())) {
                long diasAtraso = ChronoUnit.DAYS.between(
                        emprestimo.getDataPrevistaDevolucao(),
                        hoje
                );
                PoliticaLeitorStrategy politica = politicaLeitorResolver.resolver(leitor.getTipo());
                multa = politica.valorMultaDiaria().multiply(BigDecimal.valueOf(diasAtraso));
            }

            emprestimo.setDataDevolucao(hoje);
            emprestimo.setValorMulta(multa);
            emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

            EmprestimoModel salvo = emprestimoRepository.save(emprestimo);
            return EmprestimoResponse.de(salvo, livro, leitor);
        }
    }

    public List<EmprestimoResponse> listarAtrasados() {
        LocalDate hoje = LocalDate.now(clock);
        return emprestimoRepository.findAll().stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATIVO && e.getDataPrevistaDevolucao().isBefore(hoje))
                .map(this::toResponse)
                .toList();
    }

    private EmprestimoResponse toResponse(EmprestimoModel emprestimo) {
        LivroModel livro = buscarLivroOuFalhar(emprestimo.getLivroId());
        LeitorModel leitor = buscarLeitorOuFalhar(emprestimo.getLeitorId());
        return EmprestimoResponse.de(emprestimo, livro, leitor);
    }

    private EmprestimoModel buscarEmprestimoOuFalhar(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo com ID " + id + " não encontrado."));
    }

    private LeitorModel buscarLeitorOuFalhar(Long id) {
        return leitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitor com ID " + id + " não encontrado."));
    }

    private LivroModel buscarLivroOuFalhar(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro com ID " + id + " não encontrado."));
    }
}