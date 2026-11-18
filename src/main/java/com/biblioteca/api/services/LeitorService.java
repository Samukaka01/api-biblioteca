package com.biblioteca.api.services;

import com.biblioteca.api.dto.requests.LeitorRequest;
import com.biblioteca.api.dto.responses.LeitorResponse;
import com.biblioteca.api.models.leitor.LeitorModel;
import com.biblioteca.api.models.leitor.TipoLeitor;
import com.biblioteca.api.repositorys.leitor.LeitorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeitorService {

    private final LeitorRepository repository;

    public LeitorService(LeitorRepository repository) {
        this.repository = repository;
    }

    public List<LeitorResponse> listar(String nome, TipoLeitor tipo) {
        return repository.findAll().stream()
                .filter(leitor -> nome == null || leitor.getNome().toLowerCase().contains(nome.toLowerCase()))
                .filter(leitor -> tipo == null || leitor.getTipo() == tipo)
                .map(LeitorResponse::de)
                .toList();
    }

    public Optional<LeitorResponse> buscarPorId(Long id) {
        return repository.findById(id)
                .map(LeitorResponse::de);
    }

    public LeitorResponse criar(LeitorRequest request) {
        // RN02: O e-mail não pode se repetir entre leitores
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalStateException("Já existe um leitor cadastrado com o e-mail " + request.email());
        }

        LeitorModel leitor = new LeitorModel(
                null,
                request.nome(),
                request.email(),
                request.tipo()
        );

        LeitorModel salvo = repository.save(leitor);
        return LeitorResponse.de(salvo);
    }

    public Optional<LeitorResponse> atualizar(Long id, LeitorRequest request) {
        return repository.findById(id)
                .map(leitorExistente -> {
                    if (repository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
                        throw new IllegalStateException("O e-mail " + request.email() + " já pertence a outro leitor.");
                    }

                    leitorExistente.setNome(request.nome());
                    leitorExistente.setEmail(request.email());
                    leitorExistente.setTipo(request.tipo());

                    LeitorModel atualizado = repository.save(leitorExistente);
                    return LeitorResponse.de(atualizado);
                });
    }

    public boolean remover(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}