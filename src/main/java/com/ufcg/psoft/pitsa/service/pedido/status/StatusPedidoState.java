package com.ufcg.psoft.pitsa.service.pedido.status;

public interface StatusPedidoState {
    void preparar();

    void finalizarPreparo();

    void enviarParaEntrega();

    void confirmarEntrega();
}