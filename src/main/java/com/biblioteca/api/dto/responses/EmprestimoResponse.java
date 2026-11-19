package com.biblioteca.api.dto.responses;

import com.biblioteca.api.models.emprestimo.EmprestimoModel;
import com.biblioteca.api.models.emprestimo.StatusEmprestimo;
import com.biblioteca.api.models.leitor.LeitorModel;
import com.biblioteca.api.models.livro.LivroModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmprestimoResponse(
        Long id,
        Long livroId,
        String titulo,
        Long leitorId,
        String leitor,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        BigDecimal valorMulta,
        StatusEmprestimo status
) {
    public static EmprestimoResponse de(EmprestimoModel model, LivroModel livro, LeitorModel leitor) {
        return new EmprestimoResponse(
                model.getId(),
                model.getLivroId(),
                livro.getTitulo(),
                model.getLeitorId(),
                leitor.getNome(),
                model.getDataEmprestimo(),
                model.getDataPrevistaDevolucao(),
                model.getDataDevolucao(),
                model.getValorMulta(),
                model.getStatus()
        );
    }
}