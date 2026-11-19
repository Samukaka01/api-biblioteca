package com.biblioteca.api.repositorys.livro;

import com.biblioteca.api.models.livro.LivroModel;
import java.util.List;
import java.util.Optional;

public interface LivroRepository {
    List<LivroModel> findAll();

    Optional<LivroModel> findById(Long id);

    Optional<LivroModel> findByIsbn(String isbn);

    boolean existsById(Long id);
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndIdNot(String isbn, Long id);

    LivroModel save(LivroModel livroModel);
    void deleteById(Long id);
}
