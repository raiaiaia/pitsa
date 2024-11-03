package com.ufcg.psoft.pitsa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.pitsa.model.enums.TipoSabor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sabor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @JsonProperty("precoGrande")
    @Column(nullable = false)
    @Positive
    private Double precoGrande;

    @JsonProperty("precoMedia")
    @Column(nullable = false)
    @Positive
    private Double precoMedia;

    @JsonProperty("disponivel")
    private Boolean disponivel;

    @JsonProperty("tipo")
    @Column(nullable = false)
    private TipoSabor tipo;

    @JsonProperty("clientesInteressados")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sabor_id")
    private Set<Cliente> clientesInteressados;

    @PrePersist
    public void setDefaultValues() {
        if (disponivel == null) {
            disponivel = true;
        }
        if (this.clientesInteressados == null) {
            this.clientesInteressados = new HashSet<>();
        }
    }

    public void addClienteInteressado(Cliente cliente) {
        this.clientesInteressados.add(cliente);
    }

    public void removeClienteInteressado(Cliente cliente) {
        this.clientesInteressados.remove(cliente);
    }

}
