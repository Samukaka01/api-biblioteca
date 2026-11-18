package com.biblioteca.api.repositorys.leitor;

import com.biblioteca.api.models.leitor.LeitorModel;
import java.util.List;
import java.util.Optional;

public interface LeitorRepository {
    List<LeitorModel> findAll();
    Optional<LeitorModel> findById(Long id);
    Optional<LeitorModel> findByNomeIgnoreCase(String nome);
    Optional<LeitorModel> findByEmailIgnoreCase(String email);
    boolean existsById(Long id);
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    LeitorModel save(LeitorModel leitorModel);
    void  deleteById(Long id);
}
