package org.emprestimos.service;

import org.emprestimos.model.Colaborador;
import org.emprestimos.repository.ColaboradorRepository;
import org.springframework.stereotype.Service;

@Service
//contem minhas regas de negócios relacionadas ao meus colaboradores.
public class ColaboradorService {
    private final ColaboradorRepository repository;

    public ColaboradorService(ColaboradorRepository repository) {
        this.repository = repository;
    }

    public Colaborador cadastrar(Colaborador colaborador){
       if(repository.existsByEmail(colaborador.getEmail())){
           throw new IllegalArgumentException("Esse E-mail já foi cadastrado, tente outro.");
       }
        repository.save(colaborador);
        return colaborador;
    }
}