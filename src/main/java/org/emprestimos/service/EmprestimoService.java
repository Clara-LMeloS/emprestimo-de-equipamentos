package org.emprestimos.service;

import jakarta.transaction.Transactional;
import org.emprestimos.dto.EmprestimoRequest;
import org.emprestimos.exception.ConflitoException;
import org.emprestimos.exception.NaoEncontradoException;
import org.emprestimos.model.Colaborador;
import org.emprestimos.model.Emprestimo;
import org.emprestimos.model.Equipamento;
import org.emprestimos.repository.EmprestimoRepository;

import java.time.LocalDate;
import java.util.List;

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
    public Emprestimo emprestar(EmprestimoRequest pedido){
        Equipamento equipamento = equipamentoService.buscar(pedido.equipamentoId());
        Colaborador colaborador = colaboradorService.buscar(pedido.colaboradorId());


        if(!equipamento.isDisponivel()){
            throw  new ConflitoException("Equipamento indisponível");
        }
        equipamento.setDisponivel(false);

        Emprestimo emprestimo = new Emprestimo();

        emprestimo.setEquipamento(equipamento);
        emprestimo.setColaborador(colaborador);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(PRAZO_EM_DIAS));
        return repository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id){
        Emprestimo emprestimo = buscar(id);
        if (emprestimo.getDataDevolucao() != null){
            throw new ConflitoException("Emprestimo já devolvido!");
        }
    emprestimo.setDataPrevistaDevolucao(LocalDate.now());
    emprestimo.getEquipamento().setDisponivel(true);
    return repository.save(emprestimo);
    }

    public List<Emprestimo> listar(boolean atrasados){
        if (Boolean.TRUE.equals(atrasados)){
            return repository.findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now());
        }
        return repository.findAll();
    }


}
