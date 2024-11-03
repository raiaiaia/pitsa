package com.ufcg.psoft.pitsa.controller;

import com.ufcg.psoft.pitsa.dto.sabor.SaborRequestDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.pitsa.service.sabor.SaborService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/sabor",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class SaborController {

    @Autowired
    SaborService<SaborRequestDTO, SaborResponseDTO> saborService;

    @PostMapping
    public ResponseEntity<SaborResponseDTO> criarSabor(
            @RequestParam Long estabelecimentoId,
            @RequestParam String codAcessoEstabelecimento,
            @RequestBody @Valid SaborRequestDTO saborRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saborService.criar(saborRequestDTO, estabelecimentoId, codAcessoEstabelecimento));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<SaborResponseDTO> recuperarSabor(
            @PathVariable Long id,
            @RequestParam Long estabelecimentoId,
            @RequestParam String codAcessoEstabelecimento) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborService.recuperar(id, estabelecimentoId, codAcessoEstabelecimento));

    }

    @GetMapping
    public ResponseEntity<List<SaborResponseDTO>> listarSabores(
            @RequestParam Long estabelecimentoId,
            @RequestParam String codAcessoEstabelecimento) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborService.listar(estabelecimentoId, codAcessoEstabelecimento));

    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<SaborResponseDTO> atualizarSabor(
            @PathVariable Long id,
            @RequestParam Long estabelecimentoId,
            @RequestParam String codAcessoEstabelecimento,
            @RequestBody @Valid SaborRequestDTO saborRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborService.atualizar(id, estabelecimentoId, codAcessoEstabelecimento, saborRequestDTO));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> removerSabor(
            @PathVariable Long id,
            @RequestParam Long estabelecimentoId,
            @RequestParam String codAcessoEstabelecimento) {
        saborService.remover(id, estabelecimentoId, codAcessoEstabelecimento);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id:[0-9]+}/disponibilidade")
    public ResponseEntity<SaborResponseDTO> atualizarDisponibilidade(
            @PathVariable Long id,
            @RequestParam Long estabelecimentoId,
            @RequestParam String codAcessoEstabelecimento,
            @RequestParam Boolean disponibilidade) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborService.atualizarDisponibilidade(id, estabelecimentoId, codAcessoEstabelecimento, disponibilidade));
    }

}