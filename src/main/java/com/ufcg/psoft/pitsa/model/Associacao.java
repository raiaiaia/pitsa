package com.ufcg.psoft.pitsa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.pitsa.model.enums.StatusAssociacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Associacao {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonProperty("estabelecimento")
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonProperty("entregador")
    @JoinColumn(name = "entregador_id")
    private Entregador entregador;

    @JsonProperty("disponibilidadeEntregador")
    private DisponibilidadeEntregador disponibilidadeEntregador;

    @JsonProperty("status")
    private StatusAssociacao status;

    @JsonProperty("ultimaEntrega")
    private LocalDateTime ultimaEntrega;

    @PrePersist
    public void setDefaultValues() {
        if (status == null) {
            status = StatusAssociacao.EM_ANALISE;
        }
    }

    public void atualizarStatus(StatusAssociacao novoStatus) {
        if (novoStatus.equals(StatusAssociacao.APROVADO)) {
            disponibilidadeEntregador = DisponibilidadeEntregador.EM_DESCANSO;
        }
        this.status = novoStatus;
    }
}
