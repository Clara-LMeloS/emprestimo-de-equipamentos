import org.emprestimos.exception.ConflitoException;
import org.junit.jupiter.api.Test;
import org.emprestimos.model.Colaborador;
import org.emprestimos.repository.ColaboradorRepository;
import org.emprestimos.service.ColaboradorService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ColaboradorTest {
        @Mock
        ColaboradorRepository repository;

        @InjectMocks
        ColaboradorService service;

    @Test
    void naoPermiteEmailDuplicado(){
        Colaborador colaborador = new Colaborador();
        colaborador.setEmail("aquiJazUmEmail@gmail.com");

        when(repository.existsByEmail("aquiJazUmEmail@gmail.com")).thenReturn(true);
        assertThrows(ConflitoException.class, () -> {
                service.cadastrar(colaborador);
        });
    }

}
