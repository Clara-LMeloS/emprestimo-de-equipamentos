package org.emprestimos.service;

import jakarta.transaction.Transactional;
import org.emprestimos.dto.EmprestimoRequest;
import org.emprestimos.exception.ConflitoException;
import org.emprestimos.exception.NaoEncontradoException;
import org.emprestimos.model.Colaborador;
import org.emprestimos.model.Emprestimo;
import org.emprestimos.model.Equipamento;
import org.emprestimos.repository.EmprestimoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class EmprestimoService {

    private final EmprestimoRepository repository;
    private final EquipamentoService equipamentoService;
    private final ColaboradorService colaboradorService;

    public EmprestimoService (EmprestimoRepository repository, EquipamentoService equipamentoService, ColaboradorService colaboradorService){
        this.repository = repository;
        this.equipamentoService = equipamentoService;
        this.colaboradorService = colaboradorService;

    }
    public Emprestimo buscar(Long id){
    return repository.findById(id).orElseThrow(()
            -> new NaoEncontradoException("Emprestimo não encontrado."));

    }

    public List<Emprestimo> listarDoColaborador(Long colaboradorId){

        colaboradorService.buscar(colaboradorId);
        return repository.findByColaboradorId(colaboradorId);

    }
    private static final int PRAZO_EM_DIAS = 14;

    @Transactional
    public Emprestimo emprestar(Long equipamentoId, Long colaboradorId){
        Equipamento equipamento = equipamentoService.buscar(equipamentoId);
        Colaborador colaborador = colaboradorService.buscar(colaboradorId);

        boolean possuiAtraso = repository.existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(colaboradorId, LocalDate.now());

        if(possuiAtraso){
            throw new ConflitoException("Esse usuário tem atrasos pendentes.");
        }

        Emprestimo emprestimo = new Emprestimo(equipamentoId, colaboradorId,LocalDate.now());
        equipamento.emprestar();
        return repository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id){
        Emprestimo emprestimo = buscar(id);
        Equipamento equipamento = equipamentoService.buscar(emprestimo.getEquipamentoId());
        if (emprestimo.getDataDevolucao() != null){
            throw new ConflitoException("Emprestimo já devolvido!");
        }
    emprestimo.devolver(LocalDate.now());
    equipamento.devolver();
    return repository.save(emprestimo);
    }

    public List<Emprestimo> listar(Boolean atrasados){
        if (Boolean.TRUE.equals(atrasados)){
            return repository.findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now());
        }
        return repository.findAll();
    }
}

