package com.ufcg.psoft.pitsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ufcg.psoft.pitsa.exception.TipoInvalidoException;

public enum TamanhoPizza {
    MEDIA,
    GRANDE;

    @JsonCreator
    public static TamanhoPizza fromString(String value) {
        for (TamanhoPizza t : TamanhoPizza.values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new TipoInvalidoException("tamanhoPizza");
    }
}
