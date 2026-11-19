package com.biblioteca.api.services.emprestimo.strategy;

import com.biblioteca.api.models.leitor.TipoLeitor;

import java.math.BigDecimal;

public interface PoliticaLeitorStrategy {

    TipoLeitor tipoSuportado();

    int limiteEmprestimosAtivos();

    int prazoEmDias();

    BigDecimal valorMultaDiaria();
}