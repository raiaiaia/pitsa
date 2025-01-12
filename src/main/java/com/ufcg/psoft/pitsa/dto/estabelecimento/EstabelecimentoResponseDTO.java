package com.ufcg.psoft.pitsa.dto.estabelecimento;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstabelecimentoResponseDTO {

    @JsonProperty("id")
    private Long id;

}
