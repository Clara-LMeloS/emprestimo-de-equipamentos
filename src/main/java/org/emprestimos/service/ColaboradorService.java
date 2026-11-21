package org.emprestimos.service;

import org.emprestimos.exception.ConflitoException;
import org.emprestimos.model.Colaborador;
import org.emprestimos.repository.ColaboradorRepository;
import org.springframework.stereotype.Service;
import org.emprestimos.exception.NaoEncontradoException;
import java.util.List;

@Service
//contem minhas regas de negócios relacionadas ao meus colaboradores.
public class ColaboradorService {
    private final ColaboradorRepository repository;

    public ColaboradorService(ColaboradorRepository repository) {
        this.repository = repository;
    }

    public Colaborador cadastrar(Colaborador colaborador){
       if(repository.existsByEmail(colaborador.getEmail())){
           throw new ConflitoException("Esse E-mail já foi cadastrado, tente outro.");
       }
        repository.save(colaborador);
        return colaborador;
    }
    //estou listando todos os meus colaboradores
    public List<Colaborador> listar(){
        return repository.findAll();
    }

    //estou buscando meu colaborador pelo ID
    public Colaborador buscar(Long id){
       return repository.findById(id).orElseThrow(()
               -> new NaoEncontradoException("Colaborador não encontrado."));

    }



}