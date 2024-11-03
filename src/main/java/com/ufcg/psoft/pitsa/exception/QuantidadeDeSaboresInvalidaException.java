package com.ufcg.psoft.pitsa.exception;

public class QuantidadeDeSaboresInvalidaException extends PitsAException {
    public QuantidadeDeSaboresInvalidaException() {
        super("A quantidade de sabores solicitada excede o valor maximo!");
    }
}
