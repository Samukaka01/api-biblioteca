package com.biblioteca.api.services;

import com.biblioteca.api.dto.requests.LivroRequest;
import com.biblioteca.api.dto.responses.LivroResponse;
import com.biblioteca.api.models.livro.LivroModel;
import com.biblioteca.api.repositorys.livro.LivroRepository;
import com.biblioteca.api.repositorys.emprestimo.EmprestimoRepository;
import com.biblioteca.api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository repository;
    private final EmprestimoRepository emprestimoRepository;

    public LivroService(LivroRepository repository, EmprestimoRepository emprestimoRepository) {
        this.repository = repository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public List<LivroResponse> listar(String autor, Boolean disponivel){
        return repository.findAll().stream()
                .filter(livro -> autor == null || livro.getAutor().toLowerCase().contains(autor.toLowerCase()))
                .filter(livro -> {
                    if (disponivel == null) {
                        return true;
                    }
                    int disponiveis = calcularDisponiveis(livro);
                    return disponivel ? disponiveis > 0 : disponiveis == 0;
                })
                .map(livro -> LivroResponse.de(livro, calcularDisponiveis(livro)))
                .toList();
    }

    public LivroResponse buscarPorId(Long id){
        LivroModel livro = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro com ID " + id + " não encontrado."));
        return LivroResponse.de(livro, calcularDisponiveis(livro));
    }

    public LivroResponse criar(LivroRequest livroRequest){
        if(repository.existsByIsbn(livroRequest.isbn())){
            throw new IllegalStateException("Já existe um livro cadastrado com o ISBN " + livroRequest.isbn());
        }

        LivroModel livro = new LivroModel(
                null,
                livroRequest.titulo(),
                livroRequest.autor(),
                livroRequest.isbn(),
                livroRequest.anoPublicacao(),
                livroRequest.quantidadeTotal()
        );

        LivroModel salvo = repository.save(livro);
        return LivroResponse.de(salvo, salvo.getQuantidadeTotal());
    }

    public LivroResponse atualizar (Long id, LivroRequest request){
        LivroModel livroExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro com ID " + id + " não encontrado para atualização."));
                if (repository.existsByIsbnAndIdNot(request.isbn(), id)) {
                    throw new IllegalStateException("O ISBN " + request.isbn() + " já pertence a outro livro.");
                }

                long emprestimosAtivos = emprestimoRepository.contarAtivosPorLivroId(id);
                if (request.quantidadeTotal() < emprestimosAtivos) {
                    throw new IllegalStateException(
                            "A quantidade total (" + request.quantidadeTotal() + ") não pode ser menor que os empréstimos ativos (" + emprestimosAtivos + ")."
                    );
                }
                    livroExistente.setTitulo(request.titulo());
                    livroExistente.setAutor(request.autor());
                    livroExistente.setIsbn(request.isbn());
                    livroExistente.setAnoPublicacao(request.anoPublicacao());
                    livroExistente.setQuantidadeTotal(request.quantidadeTotal());

                    LivroModel atualizado = repository.save(livroExistente);
                    return LivroResponse.de(atualizado, calcularDisponiveis(atualizado));
                }
    private int calcularDisponiveis(LivroModel livro) {
        int ativos = (int) emprestimoRepository.contarAtivosPorLivroId(livro.getId());
        return livro.getQuantidadeTotal() - ativos;
    }

    public void remover (Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Livro com ID " + id + " não encontrado para remoção.");
        }

        if(emprestimoRepository.existeEmprestimoAtivoPorLivroId(id)){
            throw new IllegalStateException("Não é possível remover o livro ID " + id + " pois existem empréstimos ativos.");
        }
        repository.deleteById(id);
    }
}
