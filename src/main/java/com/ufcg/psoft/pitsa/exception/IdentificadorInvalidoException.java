package com.ufcg.psoft.pitsa.exception;

public class IdentificadorInvalidoException extends PitsAException {
    public IdentificadorInvalidoException(String nome) {
        super("O id informado para o " + nome + " eh invalido!");
    }
}