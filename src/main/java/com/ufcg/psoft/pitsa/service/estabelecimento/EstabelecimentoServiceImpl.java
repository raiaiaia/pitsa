package com.ufcg.psoft.pitsa.service.estabelecimento;

import com.ufcg.psoft.pitsa.dto.estabelecimento.EstabelecimentoRequestDTO;
import com.ufcg.psoft.pitsa.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborCardapioDTO;
import com.ufcg.psoft.pitsa.exception.EntidadeNaoExisteException;
import com.ufcg.psoft.pitsa.model.Estabelecimento;
import com.ufcg.psoft.pitsa.model.Sabor;
import com.ufcg.psoft.pitsa.model.enums.TipoSabor;
import com.ufcg.psoft.pitsa.repository.EstabelecimentoRepository;
import com.ufcg.psoft.pitsa.service.pedido.PedidoService;
import com.ufcg.psoft.pitsa.validator.CodigoAcessoValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EstabelecimentoServiceImpl implements EstabelecimentoService<EstabelecimentoRequestDTO, EstabelecimentoResponseDTO> {

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CodigoAcessoValidator codigoAcessoValidator;

    @Autowired
    PedidoService<PedidoRequestDTO, PedidoResponseDTO> pedidoService;

    @Override
    @Transactional
    public EstabelecimentoResponseDTO criar(String codigoDeAcesso, EstabelecimentoRequestDTO estabelecimentoDTO) {
        codigoAcessoValidator.validar(codigoDeAcesso, estabelecimentoDTO.getCodigoAcesso());

        Estabelecimento estabelecimento = modelMapper.map(estabelecimentoDTO, Estabelecimento.class);
        estabelecimentoRepository.save(estabelecimento);
        return modelMapper.map(estabelecimento, EstabelecimentoResponseDTO.class);
    }

    @Override
    @Transactional
    public EstabelecimentoResponseDTO atualizar(Long id, String codigoAcesso, EstabelecimentoRequestDTO estabelecimentoDTO) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(codigoAcesso, estabelecimentoDTO.getCodigoAcesso());

        modelMapper.map(estabelecimentoDTO, estabelecimento);
        estabelecimentoRepository.save(estabelecimento);
        return modelMapper.map(estabelecimento, EstabelecimentoResponseDTO.class);
    }

    @Override
    @Transactional
    public void remover(Long id, String codigoAcesso) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(codigoAcesso, estabelecimento.getCodigoAcesso());

        estabelecimentoRepository.delete(estabelecimento);
    }

    @Override
    @Transactional
    public List<SaborCardapioDTO> recuperarSabores(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        List<Sabor> sabores = estabelecimento.getSabores();
        if (sabores == null || sabores.isEmpty()) {
            return new ArrayList<>();
        }
        return sabores.stream()
                .sorted(Comparator.comparing(Sabor::getDisponivel, Comparator.reverseOrder()))
                .map(SaborCardapioDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public List<SaborCardapioDTO> recuperarSaboresPorTipo(Long id, TipoSabor tipoSabor) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        List<Sabor> sabores = estabelecimento.getSabores();
        if (sabores == null || sabores.isEmpty()) {
            return new ArrayList<>();
        }
        return sabores.stream()
                .filter(s -> s.getTipo() == tipoSabor)
                .sorted(Comparator.comparing(Sabor::getDisponivel, Comparator.reverseOrder()))
                .map(SaborCardapioDTO::new)
                .toList();
    }

}
