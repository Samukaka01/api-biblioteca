package com.biblioteca.api.repositorys.livro;

import com.biblioteca.api.models.livro.LivroModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LivroRepositoryEmMemoria implements LivroRepository {

    private final Map<Long, LivroModel> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<LivroModel> findAll(){
        return List.copyOf(dados.values());
    }

    @Override
    public Optional<LivroModel> findById(Long id){
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Optional<LivroModel> findByIsbn(String isbn){
        return dados.values()
                .stream()
                .filter(livro -> livro.getIsbn().equals(isbn))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id){
        return dados.containsKey(id);
    }

    @Override
    public boolean existsByIsbn(String isbn){
        return dados.values()
                .stream()
                .anyMatch(livro -> livro.getIsbn().equals(isbn));
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, Long id){
        return dados.values()
                .stream()
                .anyMatch(livro -> livro.getIsbn().equals(isbn) && !livro.getId().equals(id));
    }

    @Override
    public LivroModel save(LivroModel livro){
        if(livro.getId() == null){
            livro.setId(sequencia.incrementAndGet());
        }
        dados.put(livro.getId(), livro);
        return livro;
    }

    @Override
    public void deleteById(Long id) {
        dados.remove(id);
    }
}


