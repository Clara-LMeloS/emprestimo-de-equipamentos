package org.emprestimos.controller;

import jakarta.validation.Valid;
import org.emprestimos.dto.EmprestimoRequest;
import org.emprestimos.model.Emprestimo;
import org.emprestimos.service.EmprestimoService;
import org.emprestimos.service.EquipamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService service;
    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Emprestimo emprestar(@RequestBody @Valid EmprestimoRequest pedido){
        return service.emprestar(pedido);
    }
    @PutMapping("/{id}/devolucao")
    public Emprestimo devolver(@PathVariable Long id){
        return service.devolver(id);
    }
    @GetMapping
    List<Emprestimo> listar(@RequestParam(required = false) Boolean atrasados){
        return service.listar(atrasados);
    }
}
// GET /equipamentos?tipo=NOTEBOOK
// GET /equipamentos?disponivel=true
// GET /emprestimos?atrasados=true
// sem nada depois do ?: todos