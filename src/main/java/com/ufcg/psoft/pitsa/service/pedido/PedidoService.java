package com.ufcg.psoft.pitsa.service.pedido;

import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.model.enums.MetodoPagamento;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;

import java.util.List;

public interface PedidoService<I, O> {

    O criar(I pedidoRequestDTO, String codigoAcesso, Long clienteId, Long estabelecimentoId);

    O atualizar(Long id, String codigoAcesso, I pedidoRequestDTO);

    void removerCliente(Long pedidoId, Long clienteId, String codigoAcessoCliente);

    void removerEstabelecimento(Long pedidoId, Long estabelecimentoId, String codigoAcessoEstabelecimento);

    O recuperarPedidoCliente(Long pedidoId, Long clienteId, String codigoAcessoCliente);

    O recuperarPedidoEstabelecimento(Long pedidoId, Long estabelecimentoId, String codigoAcessoEstabelecimento);

    List<O> listarPedidos(Long clienteId, String codigoAcessoCliente);

    List<O> listarPedidosEstabelecimento(Long estabelecimentoId, String codigoAcessoEstabelecimento);

    List<O> listarPedidosByStatus(Long id, String codAcesso, StatusPedido statusPedido);

    O confirmarPagamento(Long pedidoId, Long clienteId, String codigoAcessoCliente, MetodoPagamento metodoPagamento);

    O finalizarPreparoPedido(Long pedidoId, Long estabelecimentoId, String codigoAcessoEstabelecimento);

    void associarEntregador(Pedido pedido);

    O confirmarRecebimento(Long pedidoId, Long clienteId, String codigoAcessoCliente);

    void cancelarPedidoCliente(Long id, String codigoAcesso, Long idPedido);
}
