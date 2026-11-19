package com.biblioteca.api.controllers;

import com.biblioteca.api.dto.requests.LivroRequest;
import com.biblioteca.api.dto.responses.LivroResponse;
import com.biblioteca.api.services.LivroService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {
    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping
    public List<LivroResponse> listar(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) Boolean disponivel) {
        return livroService.listar(autor, disponivel);
    }

    @GetMapping("/{id}")
    public LivroResponse getById(@PathVariable Long id){
        return livroService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody LivroRequest livroRequest){
        LivroResponse criado = livroService.criar(livroRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public LivroResponse atualizar(
            @PathVariable Long id,
            @RequestBody LivroRequest livroRequest){
        return livroService.atualizar(id, livroRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id){
        livroService.remover(id);
    }
}
