package com.ufcg.psoft.pitsa.validator;

import com.ufcg.psoft.pitsa.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.pitsa.exception.IdentificadorInvalidoException;
import com.ufcg.psoft.pitsa.exception.OperacaoInvalidaException;
import com.ufcg.psoft.pitsa.model.Cliente;
import com.ufcg.psoft.pitsa.model.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoValidator {

    public void validaIdentificador(Long idEsperado, Long idReal, String tipoEntidade) {
        if (!idEsperado.equals(idReal)) {
            throw new IdentificadorInvalidoException(tipoEntidade);
        }
    }

    public void validaEndereco(PedidoRequestDTO pedidoRequestDTO, Cliente cliente) {
        if (pedidoRequestDTO.getEnderecoEntrega() == null || pedidoRequestDTO.getEnderecoEntrega().isEmpty())
            pedidoRequestDTO.setEnderecoEntrega(cliente.getEndereco());
    }

    public void validaPagamento(Pedido pedido) {
        if (Boolean.TRUE.equals(pedido.getStatusPagamento())) {
            throw new OperacaoInvalidaException("pagamento");
        }
    }
}
