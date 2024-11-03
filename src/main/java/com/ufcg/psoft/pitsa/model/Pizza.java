package com.ufcg.psoft.pitsa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.enums.TamanhoPizza;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("tamanho")
    @Enumerated(EnumType.STRING)
    private TamanhoPizza tamanho;

    @JsonProperty("sabor1")
    @OneToOne
    private Sabor sabor1;

    @JsonProperty("sabor2")
    @OneToOne
    private Sabor sabor2;
}
