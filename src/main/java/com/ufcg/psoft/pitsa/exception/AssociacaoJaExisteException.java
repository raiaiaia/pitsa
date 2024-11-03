package com.ufcg.psoft.pitsa.exception;

import com.ufcg.psoft.pitsa.model.Associacao;

public class AssociacaoJaExisteException extends PitsAException {
    public AssociacaoJaExisteException(Associacao associacao) {
        super(String.format("Associacao ja existe com entregador %s para esse estabelecimento com status %s", associacao.getEntregador().getNome(), associacao.getStatus()));
    }
}