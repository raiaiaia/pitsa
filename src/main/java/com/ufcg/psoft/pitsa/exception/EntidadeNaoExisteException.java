package com.ufcg.psoft.pitsa.exception;

public class EntidadeNaoExisteException extends PitsAException {
    public EntidadeNaoExisteException(String entidade) {
        super(entidade + " inexistente!");
    }
}
