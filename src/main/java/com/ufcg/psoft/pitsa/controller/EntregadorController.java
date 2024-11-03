package com.ufcg.psoft.pitsa.controller;

import com.ufcg.psoft.pitsa.dto.entregador.EntregadorRequestDTO;
import com.ufcg.psoft.pitsa.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.pitsa.service.entregador.EntregadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/entregador",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EntregadorController {

    @Autowired
    EntregadorService<EntregadorRequestDTO, EntregadorResponseDTO> entregadorService;

    @PostMapping()
    public ResponseEntity<EntregadorResponseDTO> criarEntregador(
            @RequestBody @Valid EntregadorRequestDTO entregadorRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entregadorService.criar(entregadorRequestDTO));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<EntregadorResponseDTO> recuperarEntregador(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.recuperar(id));
    }

    @GetMapping("")
    public ResponseEntity<List<EntregadorResponseDTO>> listarEntregadores() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.listar());
    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<EntregadorResponseDTO> atualizarEntregador(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid EntregadorRequestDTO entregadorRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.atualizar(id, codigoAcesso, entregadorRequestDTO));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> removerEntregador(
            @PathVariable Long id,
            @RequestParam String codigoAcesso) {
        entregadorService.remover(id, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}