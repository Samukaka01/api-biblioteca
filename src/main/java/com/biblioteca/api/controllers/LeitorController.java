package com.biblioteca.api.controllers;

import com.biblioteca.api.dto.requests.LeitorRequest;
import com.biblioteca.api.dto.responses.LeitorResponse;
import com.biblioteca.api.models.leitor.TipoLeitor;
import com.biblioteca.api.services.LeitorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/leitores")
public class LeitorController {

    private final LeitorService service;

    public LeitorController(LeitorService service) {
        this.service = service;
    }

    @GetMapping
    public List<LeitorResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoLeitor tipo) {
        return service.listar(nome, tipo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeitorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.of(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<LeitorResponse> criar(@Valid @RequestBody LeitorRequest request) {
        LeitorResponse criado = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeitorResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LeitorRequest request) {
        return ResponseEntity.of(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!service.remover(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}