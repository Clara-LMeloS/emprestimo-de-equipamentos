package org.emprestimos.exception;

//tratador central
public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String message) {

        //Quando eu crio essa exceção o código para.
        super(message);
        //Aqui o service diz o que deu errado.
    }
}
