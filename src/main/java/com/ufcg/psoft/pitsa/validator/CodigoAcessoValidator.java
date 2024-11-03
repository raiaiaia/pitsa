package com.ufcg.psoft.pitsa.validator;

import com.ufcg.psoft.pitsa.exception.CodigoDeAcessoInvalidoException;
import org.springframework.stereotype.Component;

@Component
public class CodigoAcessoValidator {

    public void validar(String codigoDeAcesso1, String codigoDeAcesso2) {
        if (!codigoDeAcesso1.equals(codigoDeAcesso2))
            throw new CodigoDeAcessoInvalidoException();
    }
}
