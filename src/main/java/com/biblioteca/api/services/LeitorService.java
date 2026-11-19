package com.biblioteca.api.services;

import com.biblioteca.api.dto.requests.LeitorRequest;
import com.biblioteca.api.dto.responses.LeitorResponse;
import com.biblioteca.api.exceptions.ResourceNotFoundException;
import com.biblioteca.api.models.leitor.LeitorModel;
import com.biblioteca.api.models.leitor.TipoLeitor;
import com.biblioteca.api.repositorys.leitor.LeitorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public LeitorResponse buscarPorId(Long id) {
        LeitorModel leitor = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitor com ID " + id + " não encontrado."));
        return LeitorResponse.de(leitor);
    }

    public LeitorResponse criar(LeitorRequest request) {
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

    public LeitorResponse atualizar(Long id, LeitorRequest request) {
        LeitorModel leitorExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitor com o ID " + id + " não encontrado para atualização."));

        if (repository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new IllegalStateException("O e-mail " + request.email() + " já pertence a outro leitor.");
        }

        leitorExistente.setNome(request.nome());
        leitorExistente.setEmail(request.email());
        leitorExistente.setTipo(request.tipo());

        LeitorModel atualizado = repository.save(leitorExistente);
        return LeitorResponse.de(atualizado);
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Leitor com ID " + id + " não encontrado.");
        }
        repository.deleteById(id);
    }
}