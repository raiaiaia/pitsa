package com.ufcg.psoft.pitsa.repository;

import com.ufcg.psoft.pitsa.model.Associacao;
import com.ufcg.psoft.pitsa.model.enums.DisponibilidadeEntregador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociacaoRepository extends JpaRepository<Associacao, Long> {
    Associacao findByEntregadorIdAndEstabelecimentoId(Long entregadorId, Long estabelecimentoId);

    List<Associacao> findAllByEstabelecimentoIdAndEntregadorId(Long estabelecimentoId, Long entregadorId);

    Associacao findFirstByDisponibilidadeEntregadorAndEstabelecimentoIdOrderByUltimaEntregaAsc(DisponibilidadeEntregador disponibilidadeEntregador, Long estabelecimentoId);

}
