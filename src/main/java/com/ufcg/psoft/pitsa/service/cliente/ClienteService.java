package com.ufcg.psoft.pitsa.service.cliente;

import com.ufcg.psoft.pitsa.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;

import java.util.List;

public interface ClienteService<I, O> {

    O criar(I clienteRequestDTO);

    O recuperar(Long id);

    List<O> listar();

    O atualizar(Long id, String codigoAcesso, I clienteRequestDTO);

    void remover(Long id, String codigoAcesso);

    SaborResponseDTO demonstrarInteresse(Long id, String codigoAcesso, Long idSabor);

    SaborResponseDTO removerInteresse(Long id, String codigoAcesso, Long idSabor);

    List<PedidoResponseDTO> listarPedidos(Long id, String codigoAcesso);

    List<PedidoResponseDTO> listarPedidosFilter(Long id, String codigoAcesso, StatusPedido statusPedido);

    PedidoResponseDTO recuperarPedido(Long id, String codigoAcesso, Long idPedido);

    void cancelarPedido(Long id, String codigoAcesso, Long idPedido);
}
