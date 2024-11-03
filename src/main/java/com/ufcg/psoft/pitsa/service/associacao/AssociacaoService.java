package com.ufcg.psoft.pitsa.service.associacao;

import com.ufcg.psoft.pitsa.model.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.pitsa.model.enums.StatusAssociacao;

public interface AssociacaoService<O> {

    O criar(Long entregadorId, String codigoAcessoEntregador, Long estabelecimentoId);

    O atualizar(Long entregadorId, String codigoAcessoEstabelecimento, Long estabelecimentoId, StatusAssociacao status);

    O atualizarDisponibilidadeEntregador(Long entregadorId, String codigoAcessoEntregador, Long associacaoId, DisponibilidadeEntregador disponibilidadeEntregador);
}
