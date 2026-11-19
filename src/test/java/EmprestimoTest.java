import org.emprestimos.model.Emprestimo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmprestimoTest {

    @Test
    void emprestimoVencidoENaoDevolvidoEstaAtrasado(){
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(1)); //infromando que venceu ontem

        assertTrue(emprestimo.isAtrasado());
    }
}
