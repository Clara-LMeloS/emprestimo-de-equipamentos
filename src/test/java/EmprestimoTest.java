import org.emprestimos.model.Colaborador;
import org.emprestimos.model.Emprestimo;
import org.emprestimos.model.Equipamento;
import org.emprestimos.repository.EmprestimoRepository;
import org.emprestimos.service.ColaboradorService;
import org.emprestimos.service.EmprestimoService;
import org.emprestimos.service.EquipamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmprestimoTest {

    @Mock
    EmprestimoRepository repository;
    @Mock
    EquipamentoService serviceE;
    @Mock
    ColaboradorService serviceC;

    @InjectMocks
    EmprestimoService emprestimoService;


    @Test
    void emprestimoVencidoENaoDevolvidoEstaAtrasado(){
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataPrevistaDevolucao(LocalDate.of(2026,11,07));

        LocalDate dataConsulta = LocalDate.of(2026,11,10);
        assertTrue(emprestimo.estaAtrasadoEm(dataConsulta));
    }

    @Test
    void oEmprestimoAindaEstaDentroDoPrazo(){
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataPrevistaDevolucao(LocalDate.of(2026,11,15));
        LocalDate dataConsulta = LocalDate.of(2026,11,10);

        assertFalse(emprestimo.estaAtrasadoEm(dataConsulta));
    }

    @Test
    void naoPermiteEmprestarEquipamentoIndisponivel(){
        Equipamento equipamento = new Equipamento();
        Colaborador colaborador = new Colaborador();

        equipamento.setDisponivel(false);

        when(serviceE.buscar(1L)).thenReturn(equipamento);// 1L numero tipo Long
        when(serviceC.buscar(1L)).thenReturn(colaborador);
        when(repository.existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(1L,LocalDate.now())).thenReturn(false);
    }

// when(repository.existsByEmail("aquiJazUmEmail@gmail.com")).thenReturn(true);
//        assertThrows(ConflitoException.class, () -> {
//                service.cadastrar(colaborador);


}
