package com.ufcg.psoft.pitsa.dto.associacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.Associacao;
import com.ufcg.psoft.pitsa.model.Entregador;
import com.ufcg.psoft.pitsa.model.Estabelecimento;
import com.ufcg.psoft.pitsa.model.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.pitsa.model.enums.StatusAssociacao;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociacaoResponseDTO {
    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @JsonProperty("estabelecimento")
    @NotBlank(message = "Estabelecimento obrigatorio")
    private Estabelecimento estabelecimento;

    @JsonProperty("entregador")
    @NotBlank(message = "Entregador obrigatorio")
    private Entregador entregador;

    @JsonProperty("status")
    private StatusAssociacao status;

    @JsonProperty("disponibilidadeEntregador")
    private DisponibilidadeEntregador disponibilidadeEntregador;

    public AssociacaoResponseDTO(Associacao associacao) {
        this.id = associacao.getId();
        this.estabelecimento = associacao.getEstabelecimento();
        this.entregador = associacao.getEntregador();
        this.status = associacao.getStatus();
        this.disponibilidadeEntregador = associacao.getDisponibilidadeEntregador();
    }
}