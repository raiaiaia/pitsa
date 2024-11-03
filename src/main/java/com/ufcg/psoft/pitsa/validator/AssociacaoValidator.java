package com.ufcg.psoft.pitsa.validator;

import com.ufcg.psoft.pitsa.exception.AssociacaoJaExisteException;
import com.ufcg.psoft.pitsa.exception.EntidadeNaoExisteException;
import com.ufcg.psoft.pitsa.exception.OperacaoInvalidaException;
import com.ufcg.psoft.pitsa.model.Associacao;
import com.ufcg.psoft.pitsa.model.enums.StatusAssociacao;
import com.ufcg.psoft.pitsa.repository.AssociacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssociacaoValidator {

    @Autowired
    AssociacaoRepository associacaoRepository;

    public void validarAtualizacaoStatus(Associacao associacao) {
        if (associacao == null) throw new EntidadeNaoExisteException("associacao");
        if (associacao.getStatus() != StatusAssociacao.EM_ANALISE) throw new OperacaoInvalidaException("status");
    }

    public void validarAtualizacaoDisponibilidade(Associacao associacao) {
        if (associacao.getStatus() != StatusAssociacao.APROVADO)
            throw new OperacaoInvalidaException("atributo disponibilidade do entregador");
    }

    public void validar(Long entregadorId, Long estabelecimentoId) {
        List<Associacao> associacoesAnteriores = associacaoRepository.findAllByEstabelecimentoIdAndEntregadorId(entregadorId, estabelecimentoId);
        associacoesAnteriores.forEach(associacao -> {
            if (!associacao.getStatus().equals(StatusAssociacao.REJEITADO))
                throw new AssociacaoJaExisteException(associacao);
        });
    }
}
