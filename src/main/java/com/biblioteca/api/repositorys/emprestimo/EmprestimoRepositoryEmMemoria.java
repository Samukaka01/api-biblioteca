package com.biblioteca.api.repositorys.emprestimo;

import com.biblioteca.api.models.emprestimo.EmprestimoModel;
import com.biblioteca.api.models.emprestimo.StatusEmprestimo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EmprestimoRepositoryEmMemoria implements EmprestimoRepository {

    private final Map<Long, EmprestimoModel> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequenciaId = new AtomicLong(0);

    @Override
    public List<EmprestimoModel> findAll() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public Optional<EmprestimoModel> findById(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public long contarAtivosPorLivroId(Long livroId) {
        return armazenamento.values().stream()
                .filter(emprestimo -> emprestimo.getLivroId().equals(livroId)
                        && emprestimo.getStatus() == StatusEmprestimo.ATIVO).count();
    }

    @Override
    public long contarAtivosPorLeitorId(Long leitorId) {
        return armazenamento.values().stream()
                .filter(e -> e.getLeitorId().equals(leitorId) && e.getStatus() == StatusEmprestimo.ATIVO)
                .count();
    }

    @Override
    public boolean existeEmprestimoAtivoPorLivroId(Long livroId) {
        return contarAtivosPorLivroId(livroId) > 0;
    }

    @Override
    public boolean existeEmprestimoAtivoPorLeitorId(Long leitorId) {
        return contarAtivosPorLeitorId(leitorId) > 0;
    }

    @Override
    public EmprestimoModel save(EmprestimoModel emprestimo){
        if(emprestimo.getId() == null){
            emprestimo.setId(sequenciaId.incrementAndGet());
        }
        armazenamento.put(emprestimo.getId(), emprestimo);
        return emprestimo;
    }

}
