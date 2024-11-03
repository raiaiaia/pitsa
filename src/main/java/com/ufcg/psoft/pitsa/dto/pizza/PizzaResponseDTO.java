package com.ufcg.psoft.pitsa.dto.pizza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.pitsa.model.Pizza;
import com.ufcg.psoft.pitsa.model.enums.TamanhoPizza;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PizzaResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("tamanho")
    private TamanhoPizza tamanho;

    @JsonProperty("sabor1")
    private SaborResponseDTO sabor1;

    @JsonProperty("sabor2")
    private SaborResponseDTO sabor2;

    public PizzaResponseDTO(Pizza pizza) {
        this.id = pizza.getId();
        this.tamanho = pizza.getTamanho();
        this.sabor1 = new SaborResponseDTO(pizza.getSabor1());
        this.sabor2 = (pizza.getSabor2() != null) ? new SaborResponseDTO(pizza.getSabor2()) : null;
    }
}
