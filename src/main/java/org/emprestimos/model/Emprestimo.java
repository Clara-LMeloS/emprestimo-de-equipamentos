package org.emprestimos.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Equipamento equipamento;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Colaborador colaborador;

    @Column(nullable = false)
    private LocalDate dataEmprestimo;

    @Column(nullable = false)
    private LocalDate dataPrevistaDevolucao;

    private LocalDate dataDevolucao; //vai ser null até voltar

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public boolean isAtrasado(){
        return dataDevolucao == null && dataPrevistaDevolucao.isBefore(LocalDate.now());

    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    /*
    Long equipamentoId;
    Long colaboradorId;
    LocalDate dataEmprestimo;
    LocalDate dataDevolucao;
    LocalDate dataPrevistaDevolucao;

    public Emprestimo(Long equipamentoId, Long colaboradorId, LocalDate hoje){
        this.equipamentoId = equipamentoId;
        this.colaboradorId = colaboradorId;
    //como eu coloquei a data no dia "hoje" os testes podem escolher uma data específica, sem depender do dia que o teste foi executado
        this.dataEmprestimo = hoje;

        this.dataPrevistaDevolucao = hoje.plusDays(14);
    }

    //Criei esse metodo para impedir uma devolucao duplicada
    public void devolver(LocalDate data){
        if(dataDevolucao != null){
    throw new IllegalStateException(); //impedimento de uma nova devolução
        }else{//permito a devolução
            dataDevolucao = data;//registro a data da devolucao
        }
    }

     */
}
