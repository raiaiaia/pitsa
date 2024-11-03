package com.ufcg.psoft.pitsa.controller;

import com.ufcg.psoft.pitsa.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.pitsa.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;
import com.ufcg.psoft.pitsa.service.cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/cliente",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClienteController {

    @Autowired
    ClienteService<ClienteRequestDTO, ClienteResponseDTO> clienteService;

    @PostMapping()
    public ResponseEntity<ClienteResponseDTO> criarCliente(
            @RequestBody @Valid ClienteRequestDTO clienteRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.criar(clienteRequestDTO));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ClienteResponseDTO> recuperarCliente(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.recuperar(id));
    }

    @GetMapping("")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listar());
    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid ClienteRequestDTO clienteRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.atualizar(id, codigoAcesso, clienteRequestDTO));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> removerCliente(
            @PathVariable Long id,
            @RequestParam String codigoAcesso) {
        clienteService.remover(id, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id:[0-9]+}/interesse-sabor")
    public ResponseEntity<SaborResponseDTO> demonstrarInteresse(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestParam Long idSabor
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.demonstrarInteresse(id, codigoAcesso, idSabor));
    }

    @PutMapping("/{id:[0-9]+}/interesse-sabor/remover")
    public ResponseEntity<SaborResponseDTO> removerInteresse(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestParam Long idSabor
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.removerInteresse(id, codigoAcesso, idSabor));
    }

    @GetMapping("/{id:[0-9]+}/meus-pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> recuperarPedidos(
            @PathVariable Long id,
            @RequestParam String codigoAcesso) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listarPedidos(id, codigoAcesso));
    }

    @GetMapping("/{id:[0-9]+}/meus-pedidos/status/{statusPedido}")
    public ResponseEntity<List<PedidoResponseDTO>> recuperarPedidosByStatus(
            @PathVariable Long id,
            @PathVariable StatusPedido statusPedido,
            @RequestParam String codigoAcesso) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listarPedidosFilter(id, codigoAcesso, statusPedido));
    }

    @GetMapping("/{id:[0-9]+}/meus-pedidos/{idPedido:[0-9]+}")
    public ResponseEntity<PedidoResponseDTO> recuperarPedido(
            @PathVariable Long id,
            @PathVariable Long idPedido,
            @RequestParam String codigoAcesso) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.recuperarPedido(id, codigoAcesso, idPedido));
    }

    @DeleteMapping("/{id:[0-9]+}/cancelar/{idPedido:[0-9]+}")
    public ResponseEntity<Void> cancelarPedido(
            @PathVariable Long id,
            @PathVariable Long idPedido,
            @RequestParam String codigoAcesso) {
        clienteService.cancelarPedido(id, codigoAcesso, idPedido);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
