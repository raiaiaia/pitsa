package com.ufcg.psoft.pitsa.service.estabelecimento;

import com.ufcg.psoft.pitsa.dto.sabor.SaborCardapioDTO;
import com.ufcg.psoft.pitsa.model.enums.TipoSabor;

import java.util.List;

public interface EstabelecimentoService<I, O> {

    O criar(String codigoDeAcesso, I estabelecimentoRequestDTO);

    O atualizar(Long id, String codigoAcesso, I estabelecimentoRequestDTO);

    void remover(Long id, String codigoAcesso);

    List<SaborCardapioDTO> recuperarSabores(Long id);

    List<SaborCardapioDTO> recuperarSaboresPorTipo(Long id, TipoSabor tipoSabor);

}
