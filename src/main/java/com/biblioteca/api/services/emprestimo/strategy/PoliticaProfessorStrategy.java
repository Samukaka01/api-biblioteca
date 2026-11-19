package com.biblioteca.api.services.emprestimo.strategy;

import com.biblioteca.api.models.leitor.TipoLeitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PoliticaProfessorStrategy implements PoliticaLeitorStrategy {

    private final int limiteEmprestimosAtivos;
    private final int prazoEmDias;
    private final BigDecimal valorMultaDiaria;

    public PoliticaProfessorStrategy(
            @Value("${biblioteca.politicas.professor.limite-emprestimos}") int limiteEmprestimosAtivos,
            @Value("${biblioteca.politicas.professor.prazo-dias}") int prazoEmDias,
            @Value("${biblioteca.politicas.professor.multa-diaria}") BigDecimal valorMultaDiaria){
        this.limiteEmprestimosAtivos = limiteEmprestimosAtivos;
        this.prazoEmDias = prazoEmDias;
        this.valorMultaDiaria = valorMultaDiaria;
    }

    @Override
    public TipoLeitor tipoSuportado() {
        return TipoLeitor.PROFESSOR;
    }

    @Override
    public int limiteEmprestimosAtivos() {
        return limiteEmprestimosAtivos;
    }

    @Override
    public int prazoEmDias() {
        return prazoEmDias;
    }

    @Override
    public BigDecimal valorMultaDiaria() {
        return valorMultaDiaria;
    }
}