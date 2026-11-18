package com.biblioteca.api.dto.requests;

import com.biblioteca.api.models.leitor.TipoLeitor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeitorRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotNull(message = "O tipo de leitor (Aluno ou Professor) é obrigatório")
        TipoLeitor tipo
){}
