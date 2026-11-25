package com.biblioteca.api.config;

import com.biblioteca.api.models.emprestimo.EmprestimoModel;
import com.biblioteca.api.models.emprestimo.StatusEmprestimo;
import com.biblioteca.api.models.leitor.LeitorModel;
import com.biblioteca.api.models.leitor.TipoLeitor;
import com.biblioteca.api.models.livro.LivroModel;
import com.biblioteca.api.repositorys.emprestimo.EmprestimoRepository;
import com.biblioteca.api.repositorys.leitor.LeitorRepository;
import com.biblioteca.api.repositorys.livro.LivroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

@Configuration
@Profile("!test")
public class CargaInicialConfig {

    @Bean
    public CommandLineRunner carregarDadosIniciais(
            LivroRepository livroRepository,
            LeitorRepository leitorRepository,
            EmprestimoRepository emprestimoRepository,
            Clock clock){
        return args -> {
            if(!livroRepository.findAll().isEmpty() || !leitorRepository.findAll().isEmpty()){
                return;
            }
            LivroModel cleanCode = livroRepository.save(new LivroModel(
                    null,
                    "Clean Code",
                    "Robert C. Martin",
                    "978-0132350884",
                    2008,
                    2
            ));

            LivroModel ddd = livroRepository.save(new LivroModel(
                    null,
                    "Domain-Driven Design",
                    "Eric Evans",
                    "978-0321125217",
                    2003,
                    1
            ));

            LivroModel springInAction = livroRepository.save(new LivroModel(
                    null,
                    "Spring in Action",
                    "Craig Walls",
                    "978-1617294945",
                    2018,
                    3
            ));

            // 2. Leitores
            LeitorModel ana = leitorRepository.save(new LeitorModel(
                    null,
                    "Ana Silva",
                    "ana.silva@exemplo.com",
                    TipoLeitor.ALUNO
            ));

            LeitorModel bruno = leitorRepository.save(new LeitorModel(
                    null,
                    "Bruno Costa",
                    "bruno.costa@exemplo.com",
                    TipoLeitor.PROFESSOR
            ));

            LeitorModel carla = leitorRepository.save(new LeitorModel(
                    null,
                    "Carla Mendes",
                    "carla.mendes@exemplo.com",
                    TipoLeitor.ALUNO
            ));

            LocalDate hoje = LocalDate.now(clock);

            // 3. Empréstimos
            // Empréstimo ativo normal (para Ana - ALUNO)
            emprestimoRepository.save(new EmprestimoModel(
                    null,
                    ana.getId(),
                    cleanCode.getId(),
                    hoje.minusDays(2),
                    hoje.plusDays(5),
                    null,
                    BigDecimal.ZERO,
                    StatusEmprestimo.ATIVO
            ));

            // Empréstimo ativo ocupando o único exemplar de DDD (para testar RN03 ao tentar emprestar DDD)
            emprestimoRepository.save(new EmprestimoModel(
                    null,
                    bruno.getId(),
                    ddd.getId(),
                    hoje.minusDays(3),
                    hoje.plusDays(11),
                    null,
                    BigDecimal.ZERO,
                    StatusEmprestimo.ATIVO
            ));

            // Empréstimo ativo atrasado (para Carla - ALUNO, aparece em GET /emprestimos/atrasados e bloqueia novo empréstimo via RN05)
            emprestimoRepository.save(new EmprestimoModel(
                    null,
                    carla.getId(),
                    springInAction.getId(),
                    hoje.minusDays(12),
                    hoje.minusDays(5),
                    null,
                    BigDecimal.ZERO,
                    StatusEmprestimo.ATIVO
            ));

            // Empréstimo já devolvido (para testar RN08 ao tentar devolver de novo)
            emprestimoRepository.save(new EmprestimoModel(
                    null,
                    ana.getId(),
                    springInAction.getId(),
                    hoje.minusDays(10),
                    hoje.minusDays(3),
                    hoje.minusDays(4),
                    BigDecimal.ZERO,
                    StatusEmprestimo.DEVOLVIDO
            ));
        };
        }
    }

