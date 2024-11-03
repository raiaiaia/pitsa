package com.ufcg.psoft.pitsa.dto.veiculo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.Veiculo;
import com.ufcg.psoft.pitsa.model.enums.TipoVeiculo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoResponseDTO {

    @JsonProperty("placaVeiculo")
    @NotBlank(message = "Placa do veículo obrigatoria")
    private String placaVeiculo;

    @JsonProperty("tipoVeiculo")
    @NotBlank(message = "Tipo do veículo obrigatorio")
    private TipoVeiculo tipoVeiculo;

    @JsonProperty("corVeiculo")
    @NotBlank(message = "Cor do veículo obrigatoria")
    private String corVeiculo;

    public VeiculoResponseDTO(Veiculo veiculo) {
        this.placaVeiculo = veiculo.getPlacaVeiculo();
        this.tipoVeiculo = veiculo.getTipoVeiculo();
        this.corVeiculo = veiculo.getCorVeiculo();
    }

}
