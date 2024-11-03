package com.ufcg.psoft.pitsa.service.pedido.pagamento;

import com.ufcg.psoft.pitsa.model.Pedido;

public class PagamentoPix implements PagamentoStrategy {
    @Override
    public void pagar(Pedido pedido) {
        Double valorDesconto = pedido.getValorPedido() * 0.05;
        pedido.setValorPedido(pedido.getValorPedido() - valorDesconto);
        pedido.setStatusPagamento(true);
    }
}
