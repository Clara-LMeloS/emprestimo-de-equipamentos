import org.emprestimos.model.Equipamento;
import org.emprestimos.repository.EquipamentoRepository;
import org.emprestimos.service.EquipamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EquipamentoTest {

    @Mock
    EquipamentoRepository repository;

    @InjectMocks
    EquipamentoService service;

    @Test
    void naoPermitePatrimonioDuplicado(){
        Equipamento equipamento = new Equipamento();
       equipamento.setPatrimonio("PAT-001");

       when(repository.existsByPatrimonio("PAT-001")).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> {
                service.cadastrar(equipamento);
        });
    }

    @Test
    void equipamentoNovoComecaDisponivel(){
        Equipamento equipamento = new Equipamento();
        equipamento.setPatrimonio("PAT-002");

       when(repository.existsByPatrimonio("PAT-002")).thenReturn(false);
       when(repository.save(equipamento)).thenReturn(equipamento);

       service.cadastrar(equipamento);
       assertTrue(equipamento.isDisponivel());
    }
}
