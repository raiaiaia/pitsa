package com.ufcg.psoft.pitsa.service.cliente;

import com.ufcg.psoft.pitsa.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.pitsa.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborRequestDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.pitsa.exception.EntidadeNaoExisteException;
import com.ufcg.psoft.pitsa.model.Cliente;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;
import com.ufcg.psoft.pitsa.repository.ClienteRepository;
import com.ufcg.psoft.pitsa.service.pedido.PedidoService;
import com.ufcg.psoft.pitsa.service.sabor.SaborService;
import com.ufcg.psoft.pitsa.validator.CodigoAcessoValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService<ClienteRequestDTO, ClienteResponseDTO> {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CodigoAcessoValidator codigoAcessoValidator;

    @Autowired
    SaborService<SaborRequestDTO, SaborResponseDTO> saborService;

    @Autowired
    PedidoService<PedidoRequestDTO, PedidoResponseDTO> pedidoService;

    @Override
    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = modelMapper.map(clienteRequestDTO, Cliente.class);
        clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    @Transactional
    public ClienteResponseDTO recuperar(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        return new ClienteResponseDTO(cliente);
    }

    @Override
    @Transactional
    public List<ClienteResponseDTO> listar() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public ClienteResponseDTO atualizar(Long id, String codigoAcesso, ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcesso);
        modelMapper.map(clienteRequestDTO, cliente);
        clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    @Transactional
    public void remover(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcesso);
        clienteRepository.delete(cliente);
    }

    @Override
    @Transactional
    public SaborResponseDTO demonstrarInteresse(Long id, String codigoAcesso, Long idSabor) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcesso);

        return saborService.demonstrarInteresse(cliente, idSabor);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidos(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcesso);
        return pedidoService.listarPedidos(id, codigoAcesso);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidosFilter(Long id, String codigoAcesso, StatusPedido statusPedido) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcesso);

        return pedidoService.listarPedidosByStatus(id, codigoAcesso, statusPedido);
    }

    @Override
    public PedidoResponseDTO recuperarPedido(Long id, String codigoAcesso, Long idPedido) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcesso);

        return pedidoService.recuperarPedidoCliente(idPedido, id, codigoAcesso);
    }


    public SaborResponseDTO removerInteresse(Long id, String codigoAcesso, Long idSabor) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcesso);

        return saborService.removerInteresse(cliente, idSabor);
    }

    @Override
    public void cancelarPedido(Long id, String codigoAcesso, Long idPedido) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcesso);

        pedidoService.cancelarPedidoCliente(id, codigoAcesso, idPedido);
    }
}
