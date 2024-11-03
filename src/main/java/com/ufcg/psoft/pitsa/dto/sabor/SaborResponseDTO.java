package com.ufcg.psoft.pitsa.dto.sabor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.Cliente;
import com.ufcg.psoft.pitsa.model.Sabor;
import com.ufcg.psoft.pitsa.model.enums.TipoSabor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaborResponseDTO {

    @JsonProperty("precoGrande")
    @Positive(message = "Preco deve ser maior que zero")
    @NotNull(message = "Preco da pizza grande é obrigatorio")
    Double precoGrande;

    @JsonProperty("precoMedia")
    @Positive(message = "Preco deve ser maior que zero")
    @NotNull(message = "Preco da pizza média é obrigatorio")
    Double precoMedia;

    @JsonProperty("disponivel")
    @AssertTrue
    Boolean disponivel;

    @JsonProperty("clientesInteressados")
    Set<Cliente> clientesInteressados;

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("tipo")
    @NotNull(message = "Tipo obrigatorio")
    private TipoSabor tipo;

    public SaborResponseDTO(Sabor sabor) {
        this.id = sabor.getId();
        this.nome = sabor.getNome();
        this.tipo = sabor.getTipo();
        this.precoMedia = sabor.getPrecoMedia();
        this.precoGrande = sabor.getPrecoGrande();
        this.disponivel = sabor.getDisponivel();
        this.clientesInteressados = sabor.getClientesInteressados();

    }

}
