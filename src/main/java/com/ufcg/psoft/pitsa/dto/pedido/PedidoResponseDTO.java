package com.ufcg.psoft.pitsa.dto.pedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.dto.pizza.PizzaResponseDTO;
import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.model.Pizza;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("clienteId")
    private Long clienteId;

    @JsonProperty("estabelecimentoId")
    private Long estabelecimentoId;

    @JsonProperty("valor")
    private Double valorPedido;

    @JsonProperty("enderecoEntrega")
    private String enderecoEntrega;

    @JsonProperty
    @Builder.Default
    private List<PizzaResponseDTO> pizzas = List.of();

    @JsonProperty("statusPagamento")
    private boolean statusPagamento;

    @JsonProperty("statusPedido")
    private StatusPedido statusPedido;

    @JsonProperty("entregadorId")
    private Long entregadorId;

    public PedidoResponseDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.clienteId = pedido.getClienteId();
        this.estabelecimentoId = pedido.getEstabelecimentoId();
        this.valorPedido = pedido.getValorPedido();
        this.enderecoEntrega = pedido.getEnderecoEntrega();
        this.pizzas = mapeiaPizzas(pedido.getPizzas());
        this.statusPagamento = pedido.getStatusPagamento();
        this.statusPedido = pedido.getStatusPedido();
        this.entregadorId = pedido.getEntregadorId();
    }

    private List<PizzaResponseDTO> mapeiaPizzas(List<Pizza> pizzas) {
        return pizzas.stream()
                .map(PizzaResponseDTO::new)
                .toList();
    }

}
