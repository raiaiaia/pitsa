package com.ufcg.psoft.pitsa.service.entregador;

import java.util.List;

public interface EntregadorService<I, O> {

    O criar(I entregadorRequestDTO);

    O recuperar(Long entregadorId);

    List<O> listar();

    O atualizar(Long entregadorId, String codAcessoEntregador, I entregadorRequestDTO);

    void remover(Long entregadorId, String codAcessoEntregador);

}