package com.ufcg.psoft.pitsa.validator;

import com.ufcg.psoft.pitsa.exception.RelacionamentoNaoExisteException;
import com.ufcg.psoft.pitsa.model.Estabelecimento;
import com.ufcg.psoft.pitsa.model.Sabor;
import org.springframework.stereotype.Component;

@Component
public class SaborExistenteValidator {

    public void validar(Long saborId, Estabelecimento estabelecimento) {
        if (estabelecimento.getSabores() == null)
            throw new RelacionamentoNaoExisteException("Sabor inexistente no estabelecimento consultado!");
        Sabor sabor = estabelecimento.getSabores().stream().filter(sab -> sab.getId().equals(saborId)).findFirst().orElse(null);
        if (sabor == null)
            throw new RelacionamentoNaoExisteException("Sabor inexistente no estabelecimento consultado!");
    }
}
