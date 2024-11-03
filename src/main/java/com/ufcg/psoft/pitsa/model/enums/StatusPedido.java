package com.ufcg.psoft.pitsa.model.enums;

import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.service.pedido.status.*;

public enum StatusPedido {
    // Maquina de estados com ENUM
    PEDIDO_RECEBIDO {
        @Override
        public StatusPedidoState estadoAtual(Pedido pedido) {
            return new RecebidoState(pedido);
        }
    },
    PEDIDO_EM_PREPARO {
        @Override
        public StatusPedidoState estadoAtual(Pedido pedido) {
            return new EmPreparoState(pedido);
        }
    },
    PEDIDO_PRONTO {
        @Override
        public StatusPedidoState estadoAtual(Pedido pedido) {
            return new ProntoState(pedido);
        }
    },
    PEDIDO_EM_ROTA {
        @Override
        public StatusPedidoState estadoAtual(Pedido pedido) {
            return new EmRotaState(pedido);
        }
    },
    PEDIDO_ENTREGUE {
        @Override
        public StatusPedidoState estadoAtual(Pedido pedido) {
            return new EntregueState(pedido);
        }
    };

    public abstract StatusPedidoState estadoAtual(Pedido pedido);
}
