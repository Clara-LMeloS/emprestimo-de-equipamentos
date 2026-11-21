package org.emprestimos.controller;

import jakarta.validation.Valid;
import org.emprestimos.model.Colaborador;
import org.emprestimos.model.Emprestimo;
import org.emprestimos.service.ColaboradorService;
import org.emprestimos.service.EmprestimoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController// estou informando ao Spring que essa classe recebe requisições HTTP e disponibiliza os endpoints(url especifica) da API
@RequestMapping("/colaboradores")
public class ColaboradorController {

    private final EmprestimoService emprestimoService;
    private final ColaboradorService service;
    public ColaboradorController(ColaboradorService service, EmprestimoService emprestimoService) {
        this.service = service;
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Colaborador cadastrar( @RequestBody @Valid Colaborador colaborador){
        return service.cadastrar(colaborador);
    }

    @GetMapping
    public List<Colaborador> listar(){
        return service.listar();
    }
    @GetMapping("/{id}")//está definindo o caminho da minha requisição

    public Colaborador buscarId(@PathVariable Long id){
        return service.buscar(id);
    }
    @GetMapping("/{id}/emprestimos")
    public List<Emprestimo> emprestimos(@PathVariable Long id){
        return emprestimoService.listarDoColaborador(id);
    }
}