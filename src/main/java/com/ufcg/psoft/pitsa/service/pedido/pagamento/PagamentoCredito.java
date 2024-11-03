package com.ufcg.psoft.pitsa.service.pedido.pagamento;

import com.ufcg.psoft.pitsa.model.Pedido;

public class PagamentoCredito implements PagamentoStrategy {
    @Override
    public void pagar(Pedido pedido) {
        pedido.setValorPedido(pedido.getValorPedido());
        pedido.setStatusPagamento(true);
    }

}
