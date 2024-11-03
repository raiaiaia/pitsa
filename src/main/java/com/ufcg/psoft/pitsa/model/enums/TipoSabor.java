package com.ufcg.psoft.pitsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ufcg.psoft.pitsa.exception.TipoInvalidoException;

public enum TipoSabor {
    DOCE,
    SALGADO;

    @JsonCreator
    public static TipoSabor fromString(String value) {
        for (TipoSabor t : TipoSabor.values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new TipoInvalidoException("tipoSabor");
    }
}
