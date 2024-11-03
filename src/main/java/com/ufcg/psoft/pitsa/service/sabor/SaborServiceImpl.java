package com.ufcg.psoft.pitsa.service.sabor;

import com.ufcg.psoft.pitsa.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.pitsa.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborRequestDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.pitsa.event.SaborDisponivelEvent;
import com.ufcg.psoft.pitsa.exception.EntidadeNaoExisteException;
import com.ufcg.psoft.pitsa.exception.InteresseBloqueadoException;
import com.ufcg.psoft.pitsa.model.Cliente;
import com.ufcg.psoft.pitsa.model.Estabelecimento;
import com.ufcg.psoft.pitsa.model.Sabor;
import com.ufcg.psoft.pitsa.repository.EstabelecimentoRepository;
import com.ufcg.psoft.pitsa.repository.SaborRepository;
import com.ufcg.psoft.pitsa.service.cliente.ClienteService;
import com.ufcg.psoft.pitsa.validator.CodigoAcessoValidator;
import com.ufcg.psoft.pitsa.validator.SaborExistenteValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SaborServiceImpl implements SaborService<SaborRequestDTO, SaborResponseDTO> {

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CodigoAcessoValidator codigoAcessoValidator;

    @Autowired
    SaborExistenteValidator saborExistenteValidator;

    @Autowired
    @Lazy
    ClienteService<ClienteRequestDTO, ClienteResponseDTO> clienteService;

    @Override
    @Transactional
    public SaborResponseDTO criar(SaborRequestDTO saborRequestDTO, Long estabelecimentoId, String codAcessoEstabelecimento) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(estabelecimento.getCodigoAcesso(), codAcessoEstabelecimento);

        Sabor sabor = modelMapper.map(saborRequestDTO, Sabor.class);
        saborRepository.save(sabor);

        estabelecimento.getSabores().add(sabor);
        estabelecimentoRepository.save(estabelecimento);

        return modelMapper.map(sabor, SaborResponseDTO.class);
    }

    @Override
    @Transactional
    public SaborResponseDTO recuperar(Long id, Long estabelecimentoId, String codAcessoEstabelecimento) {
        Sabor sabor = saborRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("sabor"));
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));

        codigoAcessoValidator.validar(estabelecimento.getCodigoAcesso(), codAcessoEstabelecimento);
        saborExistenteValidator.validar(id, estabelecimento);

        return new SaborResponseDTO(sabor);
    }

    @Override
    @Transactional
    public List<SaborResponseDTO> listar(Long estabelecimentoId, String codAcessoEstabelecimento) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(estabelecimento.getCodigoAcesso(), codAcessoEstabelecimento);
        List<Sabor> sabores = estabelecimento.getSabores();

        if (sabores == null) {
            sabores = new ArrayList<>();
            estabelecimento.setSabores(sabores);
        }

        return sabores.stream()
                .map(SaborResponseDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public SaborResponseDTO atualizar(Long saborId, Long estabelecimentoId, String codAcessoEstabelecimento, SaborRequestDTO saborRequestDTO) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(estabelecimento.getCodigoAcesso(), codAcessoEstabelecimento);

        Sabor sabor = saborRepository.findById(saborId).orElseThrow(() -> new EntidadeNaoExisteException("sabor"));
        saborExistenteValidator.validar(saborId, estabelecimento);

        modelMapper.map(saborRequestDTO, sabor);
        return modelMapper.map(saborRepository.save(sabor), SaborResponseDTO.class);
    }

    @Override
    @Transactional
    public void remover(Long id, Long estabelecimentoId, String codAcessoEstabelecimento) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(estabelecimento.getCodigoAcesso(), codAcessoEstabelecimento);
        saborRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("sabor"));
        saborExistenteValidator.validar(id, estabelecimento);

        estabelecimento.getSabores().removeIf(s -> s.getId().equals(id));
        estabelecimentoRepository.save(estabelecimento);
    }

    @Override
    @Transactional
    public SaborResponseDTO atualizarDisponibilidade(Long id, Long estabelecimentoId, String codAcessoEstabelecimento, Boolean disponibilidade) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(estabelecimento.getCodigoAcesso(), codAcessoEstabelecimento);

        Sabor sabor = saborRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("sabor"));
        saborExistenteValidator.validar(id, estabelecimento);

        sabor.setDisponivel(disponibilidade);

        // lança a notificação de disponibilidade e limpa a lista de clientes interessadosm, ja que o sabor esta disponivel
        if (Boolean.TRUE.equals(disponibilidade)) {
            disparaSaborDisponivel(new SaborDisponivelEvent(sabor));
        }

        return modelMapper.map(saborRepository.save(sabor), SaborResponseDTO.class);
    }

    @Override
    @Transactional
    public SaborResponseDTO demonstrarInteresse(Cliente cliente, Long idSabor) {
        Sabor sabor = saborRepository.findById(idSabor).orElseThrow(() -> new EntidadeNaoExisteException("sabor"));
        if (Boolean.TRUE.equals(sabor.getDisponivel())) {
            throw new InteresseBloqueadoException();
        }
        sabor.addClienteInteressado(cliente);
        return modelMapper.map(saborRepository.save(sabor), SaborResponseDTO.class);
    }

    @Transactional
    @Override
    public SaborResponseDTO removerInteresse(Cliente cliente, Long idSabor) {
        Sabor sabor = saborRepository.findById(idSabor).orElseThrow(() -> new EntidadeNaoExisteException("sabor"));
        sabor.removeClienteInteressado(cliente);
        return modelMapper.map(saborRepository.save(sabor), SaborResponseDTO.class);
    }

    private void disparaSaborDisponivel(SaborDisponivelEvent event) {
        Sabor sabor = event.getSabor();
        Set<Cliente> clientesInteressados = sabor.getClientesInteressados();
        for (Cliente cliente : clientesInteressados) {
            cliente.notificaSaborDisponivel(event);
        }
        sabor.setClientesInteressados(new HashSet<>());
    }
}
