package com.ufcg.psoft.pitsa.event;


import com.ufcg.psoft.pitsa.model.Sabor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaborDisponivelEvent {
    private Sabor sabor;
}
