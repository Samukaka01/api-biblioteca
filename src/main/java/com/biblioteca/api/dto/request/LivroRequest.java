package com.biblioteca.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LivroRequest(
        @NotBlank(message ="O título é obrigtório")
        @Size(max =150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        @NotBlank(message = "O autor é obrigatório")
        @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres")
        String autor,

        @NotBlank(message = "O ISBN é obrigatório")
        @Pattern(regexp = "^\\d{13}$", message = "O ISBN deve ter exatamente 13 dígitos numéricos")
        String isbn,

        @NotNull(message = "O ano de publicação é obrigatório")
        @Min(value = 1450, message = "O ano de publicação deve ser a partir de 1450")
        Integer anoPublicacao,

        @NotNull(message = "A quantidade total é obrigatória")
        @Min(value = 1, message = "A quantidade total deve ser maior que zero")
        Integer quantidadeTotal

) {}
