package com.biblioteca.api.repositorys.leitor;

import com.biblioteca.api.models.leitor.LeitorModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LeitorRepositoryEmMemoria implements LeitorRepository {
    private final Map<Long, LeitorModel> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<LeitorModel> findAll() {
        return List.copyOf(dados.values());
    }

    @Override
    public Optional<LeitorModel> findById(Long id){
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Optional<LeitorModel> findByNomeIgnoreCase(String nome){
        return dados.values().stream()
                .filter(leitor -> leitor.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    @Override
    public Optional<LeitorModel> findByEmailIgnoreCase(String email){
        return dados.values().stream()
                .filter(leitor -> leitor.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id){
        return dados.containsKey(id);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email){
        return dados.values().stream()
                .anyMatch(leitor -> leitor.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id) {
        return dados.values().stream()
                .anyMatch(leitor -> leitor.getEmail().equalsIgnoreCase(email) && !leitor.getId().equals(id));
    }

    @Override
    public LeitorModel save(LeitorModel leitor){
        if (leitor.getId() == null) {
            leitor.setId(sequencia.incrementAndGet());
        }
        dados.put(leitor.getId(), leitor);
        return leitor;
    }

    @Override
    public void deleteById(Long id){
        dados.remove(id);
    }
}
