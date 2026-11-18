package org.emprestimos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Emprestimo {

    public Emprestimo(){
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

}
