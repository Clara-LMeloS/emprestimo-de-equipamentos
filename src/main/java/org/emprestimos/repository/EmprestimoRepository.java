package org.emprestimos.repository;

import org.emprestimos.model.Emprestimo;

import java.time.LocalDate;
import java.util.List;

public interface EmprestimoRepository {




    boolean existsByEquipamentoId(Long equipamentoId);
    List<Emprestimo> findByColaboradorId(Long colaboradorId);
    List<Emprestimo>findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate data);
}
