package com.biblioteca.api.controllers;

import com.biblioteca.api.dto.request.LivroRequest;
import com.biblioteca.api.dto.response.LivroResponse;
import com.biblioteca.api.services.LivroService;

import jakarta.validation.Valid;

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
    public ResponseEntity<LivroResponse> getById(@PathVariable Long id){
        return ResponseEntity.of(livroService.buscarPorId(id));
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
    public ResponseEntity<LivroResponse> atualizar(
            @PathVariable Long id,
            @RequestBody LivroRequest livroRequest){
        return ResponseEntity.of(livroService.atualizar(id, livroRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LivroResponse> remover(@PathVariable Long id){
        if(!livroService.remover(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
