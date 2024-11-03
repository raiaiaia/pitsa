package com.ufcg.psoft.pitsa.controller;

import com.ufcg.psoft.pitsa.dto.estabelecimento.EstabelecimentoRequestDTO;
import com.ufcg.psoft.pitsa.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborCardapioDTO;
import com.ufcg.psoft.pitsa.model.enums.TipoSabor;
import com.ufcg.psoft.pitsa.service.estabelecimento.EstabelecimentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/estabelecimento",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EstabelecimentoController {

    @Autowired
    EstabelecimentoService<EstabelecimentoRequestDTO, EstabelecimentoResponseDTO> estabelecimentoService;

    @PostMapping()
    public ResponseEntity<EstabelecimentoResponseDTO> criarEstabelecimento(
            @RequestParam String codigoAcesso,
            @RequestBody @Valid EstabelecimentoRequestDTO estabelecimentoRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(estabelecimentoService.criar(codigoAcesso, estabelecimentoRequestDTO));
    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<EstabelecimentoResponseDTO> atualizarEstabelecimento(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid EstabelecimentoRequestDTO estabelecimentoRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentoService.atualizar(id, codigoAcesso, estabelecimentoRequestDTO));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> removerEstabelecimento(
            @PathVariable Long id,
            @RequestParam String codigoAcesso) {
        estabelecimentoService.remover(id, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{id:[0-9]+}/cardapio")
    public ResponseEntity<List<SaborCardapioDTO>> cardapio(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentoService.recuperarSabores(id));

    }

    @GetMapping("/{id:[0-9]+}/cardapio/{tipoSabor}")
    public ResponseEntity<List<SaborCardapioDTO>> cardapioPorTipo(
            @PathVariable Long id,
            @PathVariable TipoSabor tipoSabor
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentoService.recuperarSaboresPorTipo(id, tipoSabor));

    }

}
