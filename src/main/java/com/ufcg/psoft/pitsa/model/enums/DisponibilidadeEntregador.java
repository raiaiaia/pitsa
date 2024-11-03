package com.ufcg.psoft.pitsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ufcg.psoft.pitsa.exception.TipoInvalidoException;

public enum DisponibilidadeEntregador {
    EM_DESCANSO,
    ENTREGANDO,
    ATIVO;

    @JsonCreator
    public static DisponibilidadeEntregador fromString(String value) {
        for (DisponibilidadeEntregador t : DisponibilidadeEntregador.values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new TipoInvalidoException("disponibilidadeEntregador");
    }
}
