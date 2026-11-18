package com.biblioteca.api.models.leitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeitorModel {
    private Long id;
    private String nome;
    private String email;
    private TipoLeitor tipo;
}