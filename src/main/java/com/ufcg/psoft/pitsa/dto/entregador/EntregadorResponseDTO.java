package com.ufcg.psoft.pitsa.dto.entregador;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.dto.veiculo.VeiculoResponseDTO;
import com.ufcg.psoft.pitsa.model.Entregador;
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
public class EntregadorResponseDTO {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("veiculo")
    private VeiculoResponseDTO veiculoResponseDTO;

    public EntregadorResponseDTO(Entregador entregador) {
        this.id = entregador.getId();
        this.nome = entregador.getNome();
        this.veiculoResponseDTO = new VeiculoResponseDTO(entregador.getVeiculo());
    }

}
