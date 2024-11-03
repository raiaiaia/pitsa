package com.ufcg.psoft.pitsa.exception;

public class InteresseBloqueadoException extends PitsAException {
    public InteresseBloqueadoException() {
        super("Nao eh permitido demonstrar interesse em sabor ja disponivel!");
    }
}