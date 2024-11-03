package com.ufcg.psoft.pitsa.service.pedido.status;

import com.ufcg.psoft.pitsa.exception.OperacaoInvalidaException;
import com.ufcg.psoft.pitsa.model.Pedido;

public class EntregueState implements StatusPedidoState {

    @SuppressWarnings("unused")
    private final Pedido pedido;

    public EntregueState(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void preparar() {
        throw new OperacaoInvalidaException("pedido ja foi Entregue portanto");
    }

    @Override
    public void finalizarPreparo() {
        throw new OperacaoInvalidaException("pedido ja foi Entregue portanto");
    }

    @Override
    public void enviarParaEntrega() {
        throw new OperacaoInvalidaException("pedido ja foi Entregue portanto");
    }

    @Override
    public void confirmarEntrega() {
        throw new OperacaoInvalidaException("pedido ja foi Entregue portanto");
    }
}
