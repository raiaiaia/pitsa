package com.ufcg.psoft.pitsa.controller;

import com.ufcg.psoft.pitsa.dto.associacao.AssociacaoResponseDTO;
import com.ufcg.psoft.pitsa.model.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.pitsa.model.enums.StatusAssociacao;
import com.ufcg.psoft.pitsa.service.associacao.AssociacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/associacao",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class AssociacaoController {
    @Autowired
    AssociacaoService<AssociacaoResponseDTO> associacaoService;

    @PostMapping()
    public ResponseEntity<AssociacaoResponseDTO> criarAssociacao(
            @RequestParam Long entregadorId,
            @RequestParam String codigoAcessoEntregador,
            @RequestParam Long estabelecimentoId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(associacaoService.criar(entregadorId, codigoAcessoEntregador, estabelecimentoId));
    }

    @PutMapping()
    public ResponseEntity<AssociacaoResponseDTO> atualizarStatus(
            @RequestParam Long entregadorId,
            @RequestParam String codigoAcessoEstabelecimento,
            @RequestParam Long estabelecimentoId,
            @RequestParam StatusAssociacao statusAssociacao) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(associacaoService.atualizar(entregadorId, codigoAcessoEstabelecimento, estabelecimentoId, statusAssociacao));
    }

    @PutMapping("/atualizar-disponibilidade-entregador")
    public ResponseEntity<AssociacaoResponseDTO> atualizarDisponibilidadeEntregador(
            @RequestParam Long associacaoId,
            @RequestParam Long entregadorId,
            @RequestParam String codigoAcessoEntregador,
            @RequestParam DisponibilidadeEntregador disponibilidadeEntregador) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(associacaoService.atualizarDisponibilidadeEntregador(entregadorId, codigoAcessoEntregador, associacaoId, disponibilidadeEntregador));
    }

}

