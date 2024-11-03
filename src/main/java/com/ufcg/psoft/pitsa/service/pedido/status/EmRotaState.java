package com.ufcg.psoft.pitsa.service.pedido.status;

import com.ufcg.psoft.pitsa.exception.OperacaoInvalidaException;
import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;

public class EmRotaState implements StatusPedidoState {

    private final Pedido pedido;

    public EmRotaState(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void preparar() {
        throw new OperacaoInvalidaException("pedido ja esta Em Rota portanto");
    }

    @Override
    public void finalizarPreparo() {
        throw new OperacaoInvalidaException("pedido ja esta Em Rota portanto");
    }

    @Override
    public void enviarParaEntrega() {
        throw new OperacaoInvalidaException("pedido ja esta Em Rota portanto");
    }

    @Override
    public void confirmarEntrega() {
        pedido.setStatusPedidoState(new EntregueState(pedido));
        pedido.setStatusPedido(StatusPedido.PEDIDO_ENTREGUE);
    }
}
