package com.ufcg.psoft.pitsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.event.EntregadorIndisponivelEvent;
import com.ufcg.psoft.pitsa.event.PedidoEmRotaEvent;
import com.ufcg.psoft.pitsa.event.SaborDisponivelEvent;
import com.ufcg.psoft.pitsa.listener.EntregadorIndisponivelListener;
import com.ufcg.psoft.pitsa.listener.PedidoEmRotaListener;
import com.ufcg.psoft.pitsa.listener.SaborDisponivelListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente implements SaborDisponivelListener, PedidoEmRotaListener, EntregadorIndisponivelListener {

    private static final Logger logger = LogManager.getLogger(Cliente.class);

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @JsonProperty("endereco")
    @Column(nullable = false)
    private String endereco;

    @JsonIgnore
    @Column(nullable = false)
    @Size(min = 6, max = 6, message = "Codigo de acesso deve ter exatamente 6 digitos numericos")
    private String codigoAcesso;

    @Override
    public void notificaSaborDisponivel(SaborDisponivelEvent event) {
        logger.info("Cliente {}, o sabor {} de seu interesse está disponível!", this.getNome(), event.getSabor().getNome());
    }

    @Override
    public void notificaPedidoEmRota(PedidoEmRotaEvent event) {
        Entregador entregador = event.getEntregador();
        Veiculo veiculo = entregador.getVeiculo();

        if (logger.isInfoEnabled()) {
            logger.info("""
                            {}, o seu pedido {} saiu para entrega!
                            Entregador: {}
                            Veículo: {}
                            Cor do veículo: {}
                            Placa do veículo: {}
                            """,
                    this.getNome(),
                    event.getPedido().getId(),
                    entregador.getNome(),
                    veiculo.getTipoVeiculo().toString().toLowerCase(),
                    veiculo.getCorVeiculo(),
                    veiculo.getPlacaVeiculo()
            );
        }

    }

    @Override
    public void notificaEntregadorIndisponivel(EntregadorIndisponivelEvent event) {
        logger.info("Cliente " + this.getNome() + ", os entregadores estao indisponiveis, por favor aguarde ate que alguem possa trazer seu pedido " + event.getPedido().getId());
    }
}
