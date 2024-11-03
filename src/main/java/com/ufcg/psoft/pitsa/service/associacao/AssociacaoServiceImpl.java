package com.ufcg.psoft.pitsa.service.associacao;

import com.ufcg.psoft.pitsa.dto.associacao.AssociacaoResponseDTO;
import com.ufcg.psoft.pitsa.exception.EntidadeNaoExisteException;
import com.ufcg.psoft.pitsa.model.Associacao;
import com.ufcg.psoft.pitsa.model.Entregador;
import com.ufcg.psoft.pitsa.model.Estabelecimento;
import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.model.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.pitsa.model.enums.StatusAssociacao;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;
import com.ufcg.psoft.pitsa.repository.AssociacaoRepository;
import com.ufcg.psoft.pitsa.repository.EntregadorRepository;
import com.ufcg.psoft.pitsa.repository.EstabelecimentoRepository;
import com.ufcg.psoft.pitsa.repository.PedidoRepository;
import com.ufcg.psoft.pitsa.service.pedido.PedidoServiceImpl;
import com.ufcg.psoft.pitsa.validator.AssociacaoValidator;
import com.ufcg.psoft.pitsa.validator.CodigoAcessoValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssociacaoServiceImpl implements AssociacaoService<AssociacaoResponseDTO> {

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CodigoAcessoValidator codigoAcessoValidator;

    @Autowired
    AssociacaoValidator associacaoValidator;

    @Autowired
    PedidoServiceImpl pedidoService;

    @Override
    @Transactional
    public AssociacaoResponseDTO criar(Long entregadorId, String codigoAcessoEntregador, Long estabelecimentoId) {
        //valida entregador e estabelecimento
        Entregador entregador = entregadorRepository.findById(entregadorId).orElseThrow(() -> new EntidadeNaoExisteException("entregador"));
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(entregador.getCodigoAcesso(), codigoAcessoEntregador);
        associacaoValidator.validar(entregadorId, estabelecimentoId);

        Associacao associacao = Associacao.builder()
                .entregador(entregador)
                .estabelecimento(estabelecimento)
                .build();

        return modelMapper.map(associacaoRepository.save(associacao), AssociacaoResponseDTO.class);
    }

    @Override
    @Transactional
    public AssociacaoResponseDTO atualizar(Long entregadorId, String codigoAcessoEstabelecimento, Long estabelecimentoId, StatusAssociacao status) {
        //valida entregador e estabelecimento
        entregadorRepository.findById(entregadorId).orElseThrow(() -> new EntidadeNaoExisteException("entregador"));
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(estabelecimento.getCodigoAcesso(), codigoAcessoEstabelecimento);

        //valida e altera associacao
        Associacao associacao = associacaoRepository.findByEntregadorIdAndEstabelecimentoId(entregadorId, estabelecimentoId);
        associacaoValidator.validarAtualizacaoStatus(associacao);
        associacao.atualizarStatus(status);

        return modelMapper.map(associacaoRepository.save(associacao), AssociacaoResponseDTO.class);
    }

    @Override
    @Transactional
    public AssociacaoResponseDTO atualizarDisponibilidadeEntregador(Long entregadorId, String codigoAcessoEntregador, Long associacaoId, DisponibilidadeEntregador disponibilidadeEntregador) {
        //valida entregador
        Entregador entregador = entregadorRepository.findById(entregadorId).orElseThrow(() -> new EntidadeNaoExisteException("entregador"));
        codigoAcessoValidator.validar(entregador.getCodigoAcesso(), codigoAcessoEntregador);

        //valida e altera associacao
        Associacao associacao = associacaoRepository.findById(associacaoId).orElseThrow(() -> new EntidadeNaoExisteException("associacao"));
        associacaoValidator.validarAtualizacaoDisponibilidade(associacao);
        associacao.setDisponibilidadeEntregador(disponibilidadeEntregador);
        associacaoRepository.save(associacao);

        if (associacao.getDisponibilidadeEntregador().equals(DisponibilidadeEntregador.ATIVO)) {
            //procura pelo pedido que esta a mais tempo esperando entregador
            Long estabelecimentoId = associacao.getEstabelecimento().getId();
            Pedido pedidoPronto = pedidoRepository.findFirstByEstabelecimentoIdAndStatusPedidoOrderByDataCriacaoAsc(estabelecimentoId, StatusPedido.PEDIDO_PRONTO);

            if (pedidoPronto != null) pedidoService.associarEntregador(pedidoPronto);
        }

        return modelMapper.map(associacao, AssociacaoResponseDTO.class);
    }
}
