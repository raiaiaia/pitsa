package com.ufcg.psoft.pitsa.dto.sabor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.Sabor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaborCardapioDTO {

    @JsonProperty("precoGrande")
    @Positive(message = "Preco deve ser maior que zero")
    @NotNull(message = "Preco da pizza grande é obrigatorio")
    Double precoGrande;

    @JsonProperty("precoMedia")
    @Positive(message = "Preco deve ser maior que zero")
    @NotNull(message = "Preco da pizza média é obrigatorio")
    Double precoMedia;

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("disponivel")
    private Boolean disponivel;

    public SaborCardapioDTO(Sabor sabor) {
        this.nome = sabor.getNome();
        this.precoMedia = sabor.getPrecoMedia();
        this.precoGrande = sabor.getPrecoGrande();
        this.disponivel = sabor.getDisponivel();
    }

}
