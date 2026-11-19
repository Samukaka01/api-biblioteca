package com.biblioteca.api.repositorys.emprestimo;

import com.biblioteca.api.models.emprestimo.EmprestimoModel;

import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository {
    EmprestimoModel save(EmprestimoModel emprestimo);

    List<EmprestimoModel> findAll();

    Optional<EmprestimoModel> findById(Long id);

    long contarAtivosPorLivroId(Long id);
    long contarAtivosPorLeitorId(Long id);

    boolean existeEmprestimoAtivoPorLivroId(Long id);
    boolean existeEmprestimoAtivoPorLeitorId(Long id);
}
