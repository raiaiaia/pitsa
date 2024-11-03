package com.ufcg.psoft.pitsa.dto.entregador;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.dto.veiculo.VeiculoRequestDTO;
import jakarta.validation.Valid;
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
public class EntregadorRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @Valid
    @JsonProperty("veiculo")
    @NotNull(message = "Veiculo obrigatorio")
    private VeiculoRequestDTO veiculoRequestDTO;

    @JsonProperty("codigoAcesso")
    @NotNull(message = "Codigo de acesso obrigatorio")
    @Pattern(regexp = "^\\d{6}$", message = "Codigo de acesso deve ter exatamente 6 digitos numericos")
    private String codigoAcesso;
}
