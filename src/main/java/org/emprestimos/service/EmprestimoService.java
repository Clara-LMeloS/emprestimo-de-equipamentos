package org.emprestimos.service;

import jakarta.transaction.Transactional;
import org.emprestimos.exception.ConflitoException;
import org.emprestimos.exception.NaoEncontradoException;
import org.emprestimos.model.Colaborador;
import org.emprestimos.model.Emprestimo;
import org.emprestimos.model.Equipamento;
import org.emprestimos.repository.ColaboradorRepository;
import org.emprestimos.repository.EmprestimoRepository;
import org.emprestimos.repository.EquipamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class EmprestimoService {

    private final EmprestimoRepository repository;
    private final EquipamentoRepository equipamentoRepository;
    private final ColaboradorRepository colaboradorRepository;

    public EmprestimoService (EmprestimoRepository repository, EquipamentoRepository equipamentoRepository, ColaboradorRepository colaboradorRepository){
        this.repository = repository;
        this.equipamentoRepository = equipamentoRepository;
        this.colaboradorRepository = colaboradorRepository;

    }
    public Emprestimo buscar(Long id){
    return repository.findById(id).orElseThrow(()
            -> new NaoEncontradoException("Emprestimo não encontrado."));

    }

    public List<Emprestimo> listarDoColaborador(Long colaboradorId){

        if(!colaboradorRepository.existsById(colaboradorId)){
        throw new NaoEncontradoException("Colaborador não identificado.");
        }
        return repository.findByColaboradorId(colaboradorId);

    }
    private static final int PRAZO_EM_DIAS = 14;

    @Transactional
    public Emprestimo emprestar(Long equipamentoId, Long colaboradorId) {
        Equipamento equipamento = equipamentoRepository.findById(equipamentoId)
                .orElseThrow(() -> new NaoEncontradoException("Equipamento não encontrado."));

        if (!colaboradorRepository.existsById(colaboradorId)){
            throw new NaoEncontradoException("Colaborador não encontrado.");
        }
        boolean possuiAtraso = repository.existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(
                colaboradorId, LocalDate.now());
        if (possuiAtraso) {
            throw new ConflitoException("Esse usuário tem atrasos pendentes.");
        }

        Emprestimo emprestimo = new Emprestimo(equipamentoId, colaboradorId, LocalDate.now());

        equipamento.emprestar();
        equipamentoRepository.save(equipamento);
        return repository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id) {
        Emprestimo emprestimo = buscar(id);

        Equipamento equipamento = equipamentoRepository.findById(emprestimo.getEquipamentoId())
                .orElseThrow(() -> new NaoEncontradoException("Equipamento não encontrado."));

        if (emprestimo.getDataDevolucao() != null){
            throw new ConflitoException("Emprestimo já devolvido!");
        }

        emprestimo.devolver(LocalDate.now());
        equipamento.devolver();
        equipamentoRepository.save(equipamento);

        return repository.save(emprestimo);
    }
}

