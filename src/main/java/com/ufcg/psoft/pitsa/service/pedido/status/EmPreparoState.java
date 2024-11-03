package com.ufcg.psoft.pitsa.service.pedido.status;

import com.ufcg.psoft.pitsa.exception.OperacaoInvalidaException;
import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;

public class EmPreparoState implements StatusPedidoState {

    private final Pedido pedido;

    public EmPreparoState(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void preparar() {
        throw new OperacaoInvalidaException("pedido ja esta Em Preparo portanto");
    }

    @Override
    public void finalizarPreparo() {
        pedido.setStatusPedidoState(new ProntoState(pedido));
        pedido.setStatusPedido(StatusPedido.PEDIDO_PRONTO);
    }

    @Override
    public void enviarParaEntrega() {
        throw new OperacaoInvalidaException("pedido ainda esta Em Preparo portanto");
    }

    @Override
    public void confirmarEntrega() {
        throw new OperacaoInvalidaException("pedido ainda esta Em Preparo portanto");
    }
}
