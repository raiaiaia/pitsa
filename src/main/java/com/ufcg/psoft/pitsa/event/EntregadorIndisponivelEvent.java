package com.ufcg.psoft.pitsa.event;

import com.ufcg.psoft.pitsa.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EntregadorIndisponivelEvent {
    private Pedido pedido;
}
