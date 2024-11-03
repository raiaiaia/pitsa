package com.ufcg.psoft.pitsa.dto.veiculo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.enums.TipoVeiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoRequestDTO {

    @JsonProperty("placaVeiculo")
    @Pattern(regexp = "^([A-Z]{3}-\\d{4}|[A-Z]{3}-\\d[A-Z]\\d{2})$", message = "Placa do veiculo deve ser no formato AAA-1234 ou AAA-1A23")
    @NotBlank(message = "Placa do veiculo obrigatoria")
    private String placaVeiculo;

    @JsonProperty("tipoVeiculo")
    @NotNull(message = "Tipo do veiculo obrigatorio")
    private TipoVeiculo tipoVeiculo;

    @JsonProperty("corVeiculo")
    @NotBlank(message = "Cor do veiculo obrigatoria")
    private String corVeiculo;

}
