package com.ufcg.psoft.pitsa.service.entregador;

import com.ufcg.psoft.pitsa.dto.entregador.EntregadorRequestDTO;
import com.ufcg.psoft.pitsa.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.pitsa.exception.EntidadeNaoExisteException;
import com.ufcg.psoft.pitsa.model.Entregador;
import com.ufcg.psoft.pitsa.repository.EntregadorRepository;
import com.ufcg.psoft.pitsa.validator.CodigoAcessoValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntregadorServiceImpl implements EntregadorService<EntregadorRequestDTO, EntregadorResponseDTO> {

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CodigoAcessoValidator codigoAcessoValidator;

    @Override
    @Transactional
    public EntregadorResponseDTO criar(EntregadorRequestDTO entregadorRequestDTO) {
        Entregador entregador = modelMapper.map(entregadorRequestDTO, Entregador.class);
        entregadorRepository.save(entregador);
        return modelMapper.map(entregador, EntregadorResponseDTO.class);
    }

    @Override
    @Transactional
    public EntregadorResponseDTO recuperar(Long id) {
        Entregador entregador = entregadorRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("entregador"));
        return new EntregadorResponseDTO(entregador);
    }

    @Override
    @Transactional
    public List<EntregadorResponseDTO> listar() {
        List<Entregador> entregadors = entregadorRepository.findAll();
        return entregadors.stream()
                .map(EntregadorResponseDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public EntregadorResponseDTO atualizar(Long id, String codigoAcesso, EntregadorRequestDTO entregadorRequestDTO) {
        Entregador entregador = entregadorRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("entregador"));
        codigoAcessoValidator.validar(entregador.getCodigoAcesso(), codigoAcesso);
        modelMapper.map(entregadorRequestDTO, entregador);
        entregadorRepository.save(entregador);
        return modelMapper.map(entregador, EntregadorResponseDTO.class);
    }

    @Override
    @Transactional
    public void remover(Long id, String codigoAcesso) {
        Entregador entregador = entregadorRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("entregador"));
        codigoAcessoValidator.validar(entregador.getCodigoAcesso(), codigoAcesso);
        entregadorRepository.delete(entregador);
    }

}