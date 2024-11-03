package com.ufcg.psoft.pitsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ufcg.psoft.pitsa.exception.TipoInvalidoException;

public enum TipoVeiculo {
    CARRO,
    MOTO;

    @JsonCreator
    public static TipoVeiculo fromString(String value) {
        for (TipoVeiculo tipoVeiculo : TipoVeiculo.values()) {
            if (tipoVeiculo.name().equalsIgnoreCase(value)) {
                return tipoVeiculo;
            }
        }
        throw new TipoInvalidoException("tipoVeiculo");
    }
}
