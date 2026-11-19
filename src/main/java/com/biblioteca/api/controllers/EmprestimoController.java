package com.biblioteca.api.controllers;

import com.biblioteca.api.dto.requests.EmprestimoRequest;
import com.biblioteca.api.dto.responses.EmprestimoResponse;
import com.biblioteca.api.services.emprestimo.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponse> criar(@Valid @RequestBody EmprestimoRequest request) {
        EmprestimoResponse resposta = emprestimoService.realizarEmprestimo(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.id())
                .toUri();
        return ResponseEntity.created(location).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoResponse>> listar(
            @RequestParam(required = false) Long leitorId,
            @RequestParam(required = false) Boolean ativos) {
        return ResponseEntity.ok(emprestimoService.listar(leitorId, ativos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    @PostMapping("/{id}/devolucao")
    public ResponseEntity<EmprestimoResponse> registrarDevolucao(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.registrarDevolucao(id));
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<EmprestimoResponse>> listarAtrasados() {
        return ResponseEntity.ok(emprestimoService.listarAtrasados());
    }
}