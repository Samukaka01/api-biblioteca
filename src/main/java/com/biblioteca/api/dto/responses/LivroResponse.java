package com.biblioteca.api.dto.responses;

import com.biblioteca.api.models.livro.LivroModel;

public record LivroResponse(
        Long id,
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Integer quantidadeTotal,
        Integer quantidadeDisponivel
) {
    public static LivroResponse de(LivroModel livro, int quantidadeDisponivel) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getAnoPublicacao(),
                livro.getQuantidadeTotal(),
                quantidadeDisponivel
        );
    }
}