package com.ufcg.psoft.pitsa.dto.pizza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.enums.TamanhoPizza;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PizzaRequestDTO {

    @JsonProperty("tamanho")
    @NotNull(message = "Tamanho obrigatorio")
    private TamanhoPizza tamanho;

    @JsonProperty("sabor1")
    @NotBlank(message = "Sabor1 obrigatorio")
    private String sabor1;

    @JsonProperty("sabor2")
    private String sabor2;
}