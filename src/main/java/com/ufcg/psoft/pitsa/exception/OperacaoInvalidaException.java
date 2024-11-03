package com.ufcg.psoft.pitsa.exception;

public class OperacaoInvalidaException extends PitsAException {

    public OperacaoInvalidaException(String message) {
        this(message, true);
    }

    public OperacaoInvalidaException(String message, boolean useDefaultMessage) {
        super(useDefaultMessage ? "O " + message + " nao pode ser alterado!" : message);
    }
}
