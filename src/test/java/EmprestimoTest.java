import org.emprestimos.exception.ConflitoException;
import org.emprestimos.model.Emprestimo;
import org.emprestimos.model.Equipamento;
import org.emprestimos.repository.ColaboradorRepository;
import org.emprestimos.repository.EmprestimoRepository;
import org.emprestimos.repository.EquipamentoRepository;
import org.emprestimos.service.EmprestimoService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmprestimoTest {

    @Mock
    EmprestimoRepository repository;

    @Mock
    EquipamentoRepository equipamentoRepository;

    @Mock
    ColaboradorRepository colaboradorRepository;

    @InjectMocks
    EmprestimoService emprestimoService;

    @Test
    void emprestimoVencidoENaoDevolvidoEstaAtrasado() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataPrevistaDevolucao(LocalDate.of(2026, 11, 7));

        LocalDate dataConsulta = LocalDate.of(2026, 11, 10);

        assertTrue(emprestimo.estaAtrasadoEm(dataConsulta));
    }

    @Test
    void oEmprestimoAindaEstaDentroDoPrazo() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataPrevistaDevolucao(LocalDate.of(2026, 11, 15));

        LocalDate dataConsulta = LocalDate.of(2026, 11, 10);

        assertFalse(emprestimo.estaAtrasadoEm(dataConsulta));
    }

    @Test
    void naoPermiteEmprestarEquipamentoIndisponivel() {
        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(false);

        when(equipamentoRepository.findById(1L))
                .thenReturn(Optional.of(equipamento));

        when(colaboradorRepository.existsById(1L))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                emprestimoService.emprestar(1L, 1L));
    }

    @Test
    void deveDefinirDataEmprestimoEPrazoDeDevolucao() {
        LocalDate data = LocalDate.of(2026, 11, 10);

        Emprestimo emprestimo = new Emprestimo(1L, 1L, data);

        assertEquals(data, emprestimo.getDataEmprestimo());
        assertEquals(data.plusDays(14),
                emprestimo.getDataPrevistaDevolucao());
    }

    @Test
    void naoPermiteDevolverEmprestimoMaisDeUmaVez() {
        Emprestimo emprestimo =
                new Emprestimo(1L, 1L, LocalDate.now());

        emprestimo.setDataDevolucao(LocalDate.now());

        when(repository.findById(1L))
                .thenReturn(Optional.of(emprestimo));

        Equipamento equipamento = new Equipamento();

        when(equipamentoRepository.findById(1L))
                .thenReturn(Optional.of(equipamento));

        assertThrows(ConflitoException.class, () ->
                emprestimoService.devolver(1L));

    }

    @Test
    void deveRegistrarDevolucaoELiberarEquipamento() {
        Emprestimo emprestimo =
                new Emprestimo(1L, 1L, LocalDate.now());

        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(false);

        when(repository.findById(1L))
                .thenReturn(Optional.of(emprestimo));

        when(equipamentoRepository.findById(1L))
                .thenReturn(Optional.of(equipamento));

        when(repository.save(emprestimo))
                .thenReturn(emprestimo);

        emprestimoService.devolver(1L);

        assertEquals(LocalDate.now(), emprestimo.getDataDevolucao());
        assertTrue(equipamento.isDisponivel());

        verify(equipamentoRepository).save(equipamento);
        verify(repository).save(emprestimo);
    }

    @Test
    void naoPermiteNovoEmprestimoParaColaboradorComAtraso() {
        Equipamento equipamento = new Equipamento();

        when(equipamentoRepository.findById(1L))
                .thenReturn(Optional.of(equipamento));

        when(colaboradorRepository.existsById(1L))
                .thenReturn(true);

        when(repository
                .existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(
                        1L, LocalDate.now()))
                .thenReturn(true);

        assertThrows(ConflitoException.class, () ->
                emprestimoService.emprestar(1L, 1L));
    }
};