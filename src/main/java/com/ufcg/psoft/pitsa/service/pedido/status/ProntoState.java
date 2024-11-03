package com.ufcg.psoft.pitsa.service.pedido.status;

import com.ufcg.psoft.pitsa.exception.OperacaoInvalidaException;
import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;

public class ProntoState implements StatusPedidoState {

    private final Pedido pedido;

    public ProntoState(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void preparar() {
        throw new OperacaoInvalidaException("pedido ja esta Pronto portanto");
    }

    @Override
    public void finalizarPreparo() {
        throw new OperacaoInvalidaException("pedido ja esta Pronto portanto");
    }

    @Override
    public void enviarParaEntrega() {
        pedido.setStatusPedidoState(new EmRotaState(pedido));
        pedido.setStatusPedido(StatusPedido.PEDIDO_EM_ROTA);
    }

    @Override
    public void confirmarEntrega() {
        throw new OperacaoInvalidaException("pedido Pronto ainda nao foi atribuido a um entregador portanto");
    }
}
