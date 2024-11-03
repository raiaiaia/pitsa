package com.ufcg.psoft.pitsa.exception;

public class TipoInvalidoException extends PitsAException {
    public TipoInvalidoException(String entidade) {
        super("Valor invalido para enum de " + entidade);
    }
}
