package com.ufcg.psoft.pitsa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.enums.TipoVeiculo;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {

    @JsonProperty("placaVeiculo")
    @Column(nullable = false)
    private String placaVeiculo;

    @JsonProperty("tipoVeiculo")
    @Column(nullable = false)
    private TipoVeiculo tipoVeiculo;

    @JsonProperty("corVeiculo")
    @Column(nullable = false)
    private String corVeiculo;

}
