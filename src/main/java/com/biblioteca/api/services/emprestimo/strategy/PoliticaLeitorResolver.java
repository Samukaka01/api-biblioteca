package com.biblioteca.api.services.emprestimo.strategy;

import com.biblioteca.api.models.leitor.TipoLeitor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PoliticaLeitorResolver {

    private final Map<TipoLeitor, PoliticaLeitorStrategy> estrategias;

    public PoliticaLeitorResolver(List<PoliticaLeitorStrategy> listaEstrategias) {
        this.estrategias = new EnumMap<>(TipoLeitor.class);
        for (PoliticaLeitorStrategy estrategia : listaEstrategias) {
            this.estrategias.put(estrategia.tipoSuportado(), estrategia);
        }
    }

    public PoliticaLeitorStrategy resolver(TipoLeitor tipo) {
        PoliticaLeitorStrategy estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new IllegalStateException("Política não configurada para o tipo de leitor: " + tipo);
        }
        return estrategia;
    }
}