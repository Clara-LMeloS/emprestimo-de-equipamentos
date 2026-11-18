package org.emprestimos.repository;

import org.emprestimos.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

   //estou verificando se o email ja é cadasrtado/existente no meu banco
    public boolean existsByEmail(String email);

}
