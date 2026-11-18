package org.emprestimos.model;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Emprestimo {
    private long id;
    Long equipamentoId;
    Long colaboradorId;
    LocalDate dataEmprestimo;
    LocalDate dataPrevisaoDevolucao;
    LocalDate dataDevolucao;
}
