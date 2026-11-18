package com.biblioteca.api.dto.responses;

import com.biblioteca.api.models.leitor.TipoLeitor;
import com.biblioteca.api.models.leitor.LeitorModel;

public record LeitorResponse(
        Long id,
        String nome,
        String email,
        TipoLeitor tipoLeitor
) {
    public static LeitorResponse de(LeitorModel leitorModel) {
        return new LeitorResponse(
                leitorModel.getId(),
                leitorModel.getNome(),
                leitorModel.getEmail(),
                leitorModel.getTipo()
        );
    }
}
