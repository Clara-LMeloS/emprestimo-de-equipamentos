package org.emprestimos.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long equipamentoId;
    private Long colaboradorId;

    public Long getColaboradorId() {
        return colaboradorId;
    }

    public void setColaboradorId(Long colaboradorId) {
        this.colaboradorId = colaboradorId;
    }

    public Long getEquipamentoId() {
        return equipamentoId;
    }

    public void setEquipamentoId(Long equipamentoId) {
        this.equipamentoId = equipamentoId;
    }

    @Column(nullable = false)
    private LocalDate dataEmprestimo;

    @Column(nullable = false)
    private LocalDate dataPrevistaDevolucao;

    private LocalDate dataDevolucao; //vai ser null até voltar


    public boolean isAtrasado() {
        return estaAtrasadoEm(LocalDate.now());

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


    public Emprestimo(Long equipamentoId, Long colaboradorId, LocalDate hoje) {
        this.equipamentoId = equipamentoId;
        this.colaboradorId = colaboradorId;
        //como eu coloquei a data no dia "hoje" os testes podem escolher uma data específica, sem depender do dia que o teste foi executado
        this.dataEmprestimo = hoje;

        this.dataPrevistaDevolucao = hoje.plusDays(14);
    }

    //Criei esse metodo para impedir uma devolucao duplicada
    public void devolver(LocalDate data) {
        if (dataDevolucao != null) {
            throw new IllegalStateException(); //impedimento de uma nova devolução
        } else {//permito a devolução
            dataDevolucao = data;//registro a data da devolucao
        }
    }
    public Emprestimo() {
    }

    public boolean estaAtrasadoEm(LocalDate dataConsulta) {
        return dataDevolucao == null && dataPrevistaDevolucao.isBefore(dataConsulta);
    }
}