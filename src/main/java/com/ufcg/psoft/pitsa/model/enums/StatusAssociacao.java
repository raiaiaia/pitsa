package com.ufcg.psoft.pitsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ufcg.psoft.pitsa.exception.TipoInvalidoException;

public enum StatusAssociacao {
    EM_ANALISE,
    APROVADO,
    REJEITADO;

    @JsonCreator
    public static StatusAssociacao fromString(String value) {
        for (StatusAssociacao statusAssociacao : StatusAssociacao.values()) {
            if (statusAssociacao.name().equalsIgnoreCase(value)) {
                return statusAssociacao;
            }
        }

        throw new TipoInvalidoException("StatusAssociacao");
    }
}
