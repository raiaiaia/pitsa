package com.ufcg.psoft.pitsa.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;
import com.ufcg.psoft.pitsa.service.pedido.status.StatusPedidoState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("clienteId")
    private Long clienteId;

    @JsonProperty("estabelecimentoId")
    private Long estabelecimentoId;

    @JsonProperty("entregadorId")
    private Long entregadorId;

    @JsonProperty("valor")
    private Double valorPedido;

    @JsonProperty("enderecoEntrega")
    private String enderecoEntrega;

    @JsonProperty("statusPedido")
    @Enumerated(EnumType.STRING)
    private StatusPedido statusPedido;

    @Transient
    private StatusPedidoState statusPedidoState;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    @JoinColumn(name = "pedido_id")
    @JsonProperty("pizzas")
    private List<Pizza> pizzas;

    @JsonProperty("statusPagamento")
    private Boolean statusPagamento;

    @JsonProperty("dataCriacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    public void setDefaultValues() {
        if (statusPedido == null) {
            this.statusPedido = StatusPedido.PEDIDO_RECEBIDO;
            this.statusPedidoState = StatusPedido.PEDIDO_RECEBIDO.estadoAtual(this);
        }
    }

    @PostLoad
    public void loadState() {
        // Carrega o estado com o Pedido armazenado
        this.statusPedidoState = this.statusPedido.estadoAtual(this);
    }


}
