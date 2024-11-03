package com.ufcg.psoft.pitsa.dto.pedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.dto.pizza.PizzaRequestDTO;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @JsonProperty("enderecoEntrega")
    private String enderecoEntrega;

    @JsonProperty("pizzas")
    @NotEmpty(message = "Pizzas obrigatorias")
    private List<PizzaRequestDTO> pizzas;
}
