package com.ufcg.psoft.pitsa.service.pedido.pagamento;

import com.ufcg.psoft.pitsa.model.Pedido;

public interface PagamentoStrategy {
    void pagar(Pedido pedido);
}
