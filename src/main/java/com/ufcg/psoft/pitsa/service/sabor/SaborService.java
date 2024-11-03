package com.ufcg.psoft.pitsa.service.sabor;

import com.ufcg.psoft.pitsa.model.Cliente;

import java.util.List;

public interface SaborService<I, O> {

    O criar(I saborRequestDTO, Long estabelecimentoId, String codAcessoEstabelecimento);

    O recuperar(Long id, Long estabelecimentoId, String codAcessoEstabelecimento);

    List<O> listar(Long estabelecimentoId, String codAcessoEstabelecimento);

    O atualizar(Long saborId, Long estabelecimentoId, String codAcessoEstabelecimento, I saborRequestDTO);

    void remover(Long id, Long estabelecimentoId, String codAcessoEstabelecimento);

    O atualizarDisponibilidade(Long id, Long estabelecimentoId, String codAcessoEstabelecimento, Boolean novaDisponibilidade);

    O demonstrarInteresse(Cliente cliente, Long idSabor);

    O removerInteresse(Cliente cliente, Long idSabor);
}
