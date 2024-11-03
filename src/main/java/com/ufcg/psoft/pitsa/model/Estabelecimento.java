package com.ufcg.psoft.pitsa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.event.PedidoEntregueEvent;
import com.ufcg.psoft.pitsa.listener.PedidoEntregueListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estabelecimento implements PedidoEntregueListener {

    private static final Logger logger = LogManager.getLogger(Estabelecimento.class);

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("codigoAcesso")
    @Column(nullable = false)
    @Size(min = 6, max = 6, message = "Codigo de acesso deve ter exatamente 6 digitos numericos")
    private String codigoAcesso;

    @JsonProperty("sabores")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "estabelecimento_id")
    private List<Sabor> sabores;

    @Override
    public void notificaPedidoEntregue(PedidoEntregueEvent PedidoEntregueEvent) {
        logger.info("Pedido {} foi entregue com sucesso!", PedidoEntregueEvent.getPedido());
    }
}
