package com.ufcg.psoft.pitsa.service.pedido.status;

import com.ufcg.psoft.pitsa.exception.OperacaoInvalidaException;
import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;

public class RecebidoState implements StatusPedidoState {

    private final Pedido pedido;

    public RecebidoState(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void preparar() {
        pedido.setStatusPedidoState(new EmPreparoState(pedido));
        pedido.setStatusPedido(StatusPedido.PEDIDO_EM_PREPARO);
    }

    @Override
    public void finalizarPreparo() {
        throw new OperacaoInvalidaException("pedido Recebido ainda nao foi pago portanto");
    }

    @Override
    public void enviarParaEntrega() {
        throw new OperacaoInvalidaException("pedido Recebido ainda nao foi pago portanto");
    }

    @Override
    public void confirmarEntrega() {
        throw new OperacaoInvalidaException("pedido Recebido ainda nao foi pago portanto");
    }
}
