import org.emprestimos.exception.ConflitoException;
import org.emprestimos.model.Colaborador;
import org.emprestimos.model.Emprestimo;
import org.emprestimos.model.Equipamento;
import org.emprestimos.repository.EmprestimoRepository;
import org.emprestimos.repository.EquipamentoRepository;
import org.emprestimos.service.ColaboradorService;
import org.emprestimos.service.EmprestimoService;
import org.emprestimos.service.EquipamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmprestimoTest {

    @Mock
    EmprestimoRepository repository;
    @Mock
    EquipamentoService serviceE;
    @Mock
    ColaboradorService serviceC;
    @Mock
    EmprestimoRepository emprestimoRepository;
    @Mock
    EquipamentoRepository repository2;

    @InjectMocks
    EmprestimoService emprestimoService;
    @InjectMocks
    EquipamentoService service;


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

    @Test
    void deveDefinirDataEmprestimoEPrazoDeDevolucao(){
        LocalDate data = LocalDate.of(2026,11,10);
        Emprestimo emprestimo = new Emprestimo(1L,1L, data);
        assertEquals(data, emprestimo.getDataEmprestimo());
        assertEquals(data.plusDays(14), emprestimo.getDataPrevistaDevolucao());
    }

    @Test
    void naoPermiteDevolverEmprestimoMaisDeUmaVez() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.now());
        emprestimo.setDataDevolucao(LocalDate.now());

        Equipamento equipamento = new Equipamento();

        when(repository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(serviceE.buscar(1L)).thenReturn(equipamento);

        assertThrows(ConflitoException.class, () -> {
            emprestimoService.devolver(1L);
        });
    }

    @Test
    void deveRegistrarDevolucaoELiberarEquipamento() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.now());
        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(false);

        when(repository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(serviceE.buscar(1L)).thenReturn(equipamento);
        when(repository.save(emprestimo)).thenReturn(emprestimo);

        emprestimoService.devolver(1L);

        assertEquals(LocalDate.now(), emprestimo.getDataDevolucao());
        assertTrue(equipamento.isDisponivel());
    }

    @Test
    void naoPermiteRemoverEquipamentoComHistorico() {
        Equipamento equipamento = new Equipamento();

        when(repository2.findById(1L)).thenReturn(Optional.of(equipamento));
        when(emprestimoRepository.existsByEquipamentoId(1L)).thenReturn(true);

        assertThrows(ConflitoException.class, () -> {
            service.remover(1L);
        });
    }

    @Test
    void naoPermiteNovoEmprestimoParaColaboradorComAtraso() {
        Equipamento equipamento = new Equipamento();
        Colaborador colaborador = new Colaborador();

        when(serviceE.buscar(1L)).thenReturn(equipamento);
        when(serviceC.buscar(1L)).thenReturn(colaborador);

        when(repository.existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(
                1L, LocalDate.now())).thenReturn(true);

        assertThrows(ConflitoException.class, () -> {
            emprestimoService.emprestar(1L, 1L);
        });
    }

}
