package com.ufcg.psoft.pitsa.controller;

import com.ufcg.psoft.pitsa.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.pitsa.model.enums.MetodoPagamento;
import com.ufcg.psoft.pitsa.service.pedido.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/pedido",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PedidoController {

    @Autowired
    PedidoService<PedidoRequestDTO, PedidoResponseDTO> pedidoService;

    @PostMapping()
    public ResponseEntity<PedidoResponseDTO> criarPedido(
            @RequestParam Long clienteId,
            @RequestParam String codigoAcessoCliente,
            @RequestParam Long estabelecimentoId,
            @RequestBody @Valid PedidoRequestDTO pedidoRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoService.criar(pedidoRequestDTO, codigoAcessoCliente, clienteId, estabelecimentoId));
    }

    @PutMapping("/{pedidoId:[0-9]+}")
    public ResponseEntity<PedidoResponseDTO> atualizarPedido(
            @PathVariable Long pedidoId,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid PedidoRequestDTO pedidoRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.atualizar(pedidoId, codigoAcesso, pedidoRequestDTO));
    }

    @GetMapping("/{pedidoId:[0-9]+}/cliente/{clienteId:[0-9]+}")
    public ResponseEntity<PedidoResponseDTO> recuperarPedidoCliente(
            @PathVariable Long pedidoId,
            @PathVariable Long clienteId,
            @RequestParam String codigoAcessoCliente) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.recuperarPedidoCliente(pedidoId, clienteId, codigoAcessoCliente));
    }

    @GetMapping("/{pedidoId:[0-9]+}/estabelecimento/{estabelecimentoId:[0-9]+}")
    public ResponseEntity<PedidoResponseDTO> recuperarPedidoEstabelecimento(
            @PathVariable Long pedidoId,
            @PathVariable Long estabelecimentoId,
            @RequestParam String codigoAcessoEstabelecimento) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.recuperarPedidoEstabelecimento(pedidoId, estabelecimentoId, codigoAcessoEstabelecimento));
    }

    @GetMapping("/cliente/{clienteId:[0-9]+}/all")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos(
            @PathVariable Long clienteId,
            @RequestParam String codigoAcessoCliente) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.listarPedidos(clienteId, codigoAcessoCliente));
    }

    @GetMapping("/estabelecimento/{estabelecimentoId:[0-9]+}/all")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidosEstabelecimento(
            @PathVariable Long estabelecimentoId,
            @RequestParam String codigoAcessoEstabelecimento) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.listarPedidosEstabelecimento(estabelecimentoId, codigoAcessoEstabelecimento));
    }

    @DeleteMapping("/{pedidoId:[0-9]+}/cliente/{clienteId:[0-9]+}")
    public ResponseEntity<Void> removerPedidoCliente(
            @PathVariable Long pedidoId,
            @PathVariable Long clienteId,
            @RequestParam String codigoAcessoCliente) {
        pedidoService.removerCliente(pedidoId, clienteId, codigoAcessoCliente);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{pedidoId:[0-9]+}/estabelecimento/{estabelecimentoId:[0-9]+}")
    public ResponseEntity<Void> removerPedidoEstabelecimento(
            @PathVariable Long pedidoId,
            @PathVariable Long estabelecimentoId,
            @RequestParam String codigoAcessoEstabelecimento) {
        pedidoService.removerEstabelecimento(pedidoId, estabelecimentoId, codigoAcessoEstabelecimento);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{pedidoId:[0-9]+}/confirmar-pagamento")
    public ResponseEntity<PedidoResponseDTO> confirmarPagamento(
            @PathVariable Long pedidoId,
            @RequestParam Long clienteId,
            @RequestParam String codigoAcessoCliente,
            @RequestParam MetodoPagamento metodoPagamento) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.confirmarPagamento(pedidoId, clienteId, codigoAcessoCliente, metodoPagamento));
    }

    @PutMapping("/{pedidoId:[0-9]+}/finalizar-preparo-pedido")
    public ResponseEntity<PedidoResponseDTO> estabelecimentoPrepararPedido(
            @PathVariable Long pedidoId,
            @RequestParam Long estabelecimentoId,
            @RequestParam String codigoAcessoEstabelecimento) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.finalizarPreparoPedido(pedidoId, estabelecimentoId, codigoAcessoEstabelecimento));
    }

    @PutMapping("/{pedidoId:[0-9]+}/confirmar-entrega")
    public ResponseEntity<PedidoResponseDTO> clienteConfirmaEntrega(
            @PathVariable Long pedidoId,
            @RequestParam Long clienteId,
            @RequestParam String codigoAcessoCliente) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.confirmarRecebimento(pedidoId, clienteId, codigoAcessoCliente));
    }
}
