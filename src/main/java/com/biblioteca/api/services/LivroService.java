package com.biblioteca.api.services;

import com.biblioteca.api.dto.request.LivroRequest;
import com.biblioteca.api.dto.response.LivroResponse;
import com.biblioteca.api.model.LivroModel;
import com.biblioteca.api.repositorys.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public List<LivroResponse> listar(String autor, Boolean disponivel){
        return repository.findAll().stream()
                .filter(livro -> autor == null || livro.getAutor().toLowerCase().contains(autor.toLowerCase()))
                .map(livro -> LivroResponse.de(livro, livro.getQuantidadeTotal()))
                .toList();
    }

    public Optional<LivroResponse> buscarPorId(Long id){
        return repository.findById(id)
                .map(livro -> LivroResponse.de(livro, livro.getQuantidadeTotal()));
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

    public Optional<LivroResponse> atualizar (Long id, LivroRequest request){
        return repository.findById(id)
                .map(livroExistente ->{
                    if(repository.existsByIsbnAndIdNot(request.isbn(), id)){
                        throw new IllegalStateException("O ISB " + request.isbn() + " já pertence a um livro.");
                    }

                    livroExistente.setTitulo(request.titulo());
                    livroExistente.setAutor(request.autor());
                    livroExistente.setIsbn(request.isbn());
                    livroExistente.setAnoPublicacao(request.anoPublicacao());
                    livroExistente.setQuantidadeTotal(request.quantidadeTotal());

                    LivroModel atualizado = repository.save(livroExistente);
                    return LivroResponse.de(atualizado, atualizado.getQuantidadeTotal());
                });
    }

    public boolean remover (Long id){
        if(!repository.existsById(id)){
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
