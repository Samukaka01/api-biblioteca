package com.biblioteca.api.models.emprestimo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmprestimoModel {
    private Long id;
    private Long leitorId;
    private Long livroId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private BigDecimal valorMulta;
    private StatusEmprestimo status;
}
