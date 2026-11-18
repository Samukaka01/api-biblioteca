package com.biblioteca.api.dto.response;

import com.biblioteca.api.model.LivroModel;

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