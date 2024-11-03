package com.ufcg.psoft.pitsa.repository;

import com.ufcg.psoft.pitsa.model.Pedido;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findAllByClienteId(Long clienteId);

    List<Pedido> findAllByEstabelecimentoId(Long estabelecimentoId);

    List<Pedido> findAllByClienteIdAndStatusPedido(Long clienteId, StatusPedido statusPedido);

    Pedido findFirstByEstabelecimentoIdAndStatusPedidoOrderByDataCriacaoAsc(Long estabelecimentoId, StatusPedido statusPedido);
}
