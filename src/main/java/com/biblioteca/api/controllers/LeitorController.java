package com.biblioteca.api.controllers;

import com.biblioteca.api.dto.requests.LeitorRequest;
import com.biblioteca.api.dto.responses.LeitorResponse;
import com.biblioteca.api.models.leitor.TipoLeitor;
import com.biblioteca.api.services.LeitorService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
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
    public LeitorResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
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
    public LeitorResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LeitorRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}