package com.ufcg.psoft.pitsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ufcg.psoft.pitsa.exception.TipoInvalidoException;

public enum MetodoPagamento {
    CREDITO,
    DEBITO,
    PIX;

    @JsonCreator
    public static MetodoPagamento fromString(String value) {
        for (MetodoPagamento metodoPagamento : MetodoPagamento.values()) {
            if (metodoPagamento.name().equalsIgnoreCase(value)) {
                return metodoPagamento;
            }
        }

        throw new TipoInvalidoException("MetodoPagamento");
    }
}
