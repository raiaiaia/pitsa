package com.ufcg.psoft.pitsa.service.pedido;

import com.ufcg.psoft.pitsa.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.pitsa.dto.pizza.PizzaRequestDTO;
import com.ufcg.psoft.pitsa.event.EntregadorIndisponivelEvent;
import com.ufcg.psoft.pitsa.event.PedidoEmRotaEvent;
import com.ufcg.psoft.pitsa.event.PedidoEntregueEvent;
import com.ufcg.psoft.pitsa.exception.EntidadeNaoExisteException;
import com.ufcg.psoft.pitsa.exception.OperacaoInvalidaException;
import com.ufcg.psoft.pitsa.model.*;
import com.ufcg.psoft.pitsa.model.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.pitsa.model.enums.MetodoPagamento;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;
import com.ufcg.psoft.pitsa.repository.*;
import com.ufcg.psoft.pitsa.service.pedido.pagamento.PagamentoCredito;
import com.ufcg.psoft.pitsa.service.pedido.pagamento.PagamentoDebito;
import com.ufcg.psoft.pitsa.service.pedido.pagamento.PagamentoPix;
import com.ufcg.psoft.pitsa.service.pedido.pagamento.PagamentoStrategy;
import com.ufcg.psoft.pitsa.service.pizza.PizzaService;
import com.ufcg.psoft.pitsa.validator.AssociacaoValidator;
import com.ufcg.psoft.pitsa.validator.CodigoAcessoValidator;
import com.ufcg.psoft.pitsa.validator.PedidoValidator;
import com.ufcg.psoft.pitsa.validator.SaborExistenteValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Service
public class PedidoServiceImpl implements PedidoService<PedidoRequestDTO, PedidoResponseDTO> {

    private final Map<MetodoPagamento, PagamentoStrategy> pagamentoMap = Map.of(
            MetodoPagamento.CREDITO, new PagamentoCredito(),
            MetodoPagamento.DEBITO, new PagamentoDebito(),
            MetodoPagamento.PIX, new PagamentoPix()
    );
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    SaborExistenteValidator saborExistenteValidator;
    @Autowired
    PedidoValidator pedidoValidator;
    @Autowired
    CodigoAcessoValidator codigoAcessoValidator;
    @Autowired
    AssociacaoValidator associacaoValidator;
    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    SaborRepository saborRepository;
    @Autowired
    EntregadorRepository entregadorRepository;
    @Autowired
    AssociacaoRepository associacaoRepository;
    @Autowired
    private PizzaService<PizzaRequestDTO, Pizza> pizzaService;

    @Override
    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO pedidoRequestDTO, String codigoAcessoCliente, Long clienteId, Long estabelecimentoId) {
        //valida estabelecimento e cliente
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcessoCliente);

        //valida e cria pizzas
        List<Pizza> pizzas = pizzaService.validarEConverterPizzas(pedidoRequestDTO.getPizzas(), estabelecimento);

        //cria pedido
        pedidoValidator.validaEndereco(pedidoRequestDTO, cliente);
        Pedido novoPedido = modelMapper.map(pedidoRequestDTO, Pedido.class);
        setNovoPedido(novoPedido, clienteId, estabelecimentoId, pizzas);
        novoPedido.setDefaultValues();

        //salva pedido
        pedidoRepository.save(novoPedido);

        return modelMapper.map(novoPedido, PedidoResponseDTO.class);
    }

    @Override
    @Transactional
    public PedidoResponseDTO atualizar(Long id, String codigoAcessoCliente, PedidoRequestDTO pedidoRequestDTO) {
        //valida pedido, estabelecimento e cliente
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(pedido.getEstabelecimentoId()).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        Cliente cliente = clienteRepository.findById(pedido.getClienteId()).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(codigoAcessoCliente, cliente.getCodigoAcesso());

        //valida e cria pizzas
        List<Pizza> pizzas = pizzaService.validarEConverterPizzas(pedidoRequestDTO.getPizzas(), estabelecimento);

        //atualiza
        pedido.getPizzas().clear();
        pedido.getPizzas().addAll(pizzas);
        pedido.setEnderecoEntrega(pedidoRequestDTO.getEnderecoEntrega());
        pedidoRepository.save(pedido);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    @Transactional
    public void removerCliente(Long id, Long clienteId, String codigoAcessoCliente) {
        //valida cliente e pedido
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        pedidoValidator.validaIdentificador(pedido.getClienteId(), clienteId, "cliente");
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(codigoAcessoCliente, cliente.getCodigoAcesso());

        //remove
        pedidoRepository.deleteById(pedido.getId());
    }

    @Override
    @Transactional
    public void removerEstabelecimento(Long id, Long estabelecimentoId, String codigoAcessoEstabelecimento) {
        //valida estabelecimento e pedido
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        pedidoValidator.validaIdentificador(pedido.getEstabelecimentoId(), estabelecimentoId, "estabelecimento");
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(codigoAcessoEstabelecimento, estabelecimento.getCodigoAcesso());

        //remove
        pedidoRepository.deleteById(pedido.getId());
    }

    @Override
    public PedidoResponseDTO recuperarPedidoCliente(Long id, Long clienteId, String codigoAcessoCliente) {
        //valida cliente e pedido
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        pedidoValidator.validaIdentificador(pedido.getClienteId(), clienteId, "cliente");
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(codigoAcessoCliente, cliente.getCodigoAcesso());

        return new PedidoResponseDTO(pedido);
    }

    @Override
    public PedidoResponseDTO recuperarPedidoEstabelecimento(Long id, Long estabelecimentoId, String codigoAcessoEstabelecimento) {
        //valida estabelecimento e pedido
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        pedidoValidator.validaIdentificador(pedido.getEstabelecimentoId(), estabelecimentoId, "estabelecimento");
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(codigoAcessoEstabelecimento, estabelecimento.getCodigoAcesso());

        return new PedidoResponseDTO(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidos(Long clienteId, String codigoAcessoCliente) {
        //valida cliente
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(codigoAcessoCliente, cliente.getCodigoAcesso());

        return pedidoRepository.findAllByClienteId(clienteId).stream()
                .sorted(Comparator.comparing(Pedido::getStatusPedido))
                .map(PedidoResponseDTO::new)
                .toList();
    }

    @Override
    public List<PedidoResponseDTO> listarPedidosByStatus(Long clienteId, String codigoAcessoCliente, StatusPedido statusPedido) {
        //valida cliente
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(codigoAcessoCliente, cliente.getCodigoAcesso());

        List<Pedido> pedidos = pedidoRepository.findAllByClienteIdAndStatusPedido(clienteId, statusPedido);
        return mapeiaPedidos(pedidos);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidosEstabelecimento(Long estabelecimentoId, String codigoAcessoEstabelecimento) {
        //valida estabelecimento
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(codigoAcessoEstabelecimento, estabelecimento.getCodigoAcesso());

        List<Pedido> pedidos = pedidoRepository.findAllByEstabelecimentoId(estabelecimentoId);
        return mapeiaPedidos(pedidos);
    }

    @Override
    @Transactional
    public PedidoResponseDTO confirmarPagamento(Long id, Long clienteId, String codigoAcessoCliente, MetodoPagamento metodoPagamento) {
        //valida pedido e cliente
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(cliente.getCodigoAcesso(), codigoAcessoCliente);
        pedidoValidator.validaPagamento(pedido);

        //efetua pagamento
        PagamentoStrategy pagamento = pagamentoMap.get(metodoPagamento);
        pagamento.pagar(pedido);

        return prepararPedido(id);
    }

    @Transactional
    protected PedidoResponseDTO prepararPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        pedido.getStatusPedidoState().preparar();
        pedidoRepository.save(pedido);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    @Transactional
    public PedidoResponseDTO finalizarPreparoPedido(Long id, Long estabelecimentoId, String codigoAcessoEstabelecimento) {
        //valida estabelecimento e pedido
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        pedidoValidator.validaIdentificador(pedido.getEstabelecimentoId(), estabelecimentoId, "estabelecimento");
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        codigoAcessoValidator.validar(codigoAcessoEstabelecimento, estabelecimento.getCodigoAcesso());

        pedido.getStatusPedidoState().finalizarPreparo();
        associarEntregador(pedido);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    @Transactional
    public void associarEntregador(Pedido pedido) {
        Associacao associacao = associacaoRepository.findFirstByDisponibilidadeEntregadorAndEstabelecimentoIdOrderByUltimaEntregaAsc(DisponibilidadeEntregador.ATIVO, pedido.getEstabelecimentoId());
        Cliente cliente = clienteRepository.findById(pedido.getClienteId()).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));

        if (associacao == null) {
            cliente.notificaEntregadorIndisponivel(new EntregadorIndisponivelEvent(pedido));
        } else {
            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.ENTREGANDO);
            associacaoRepository.save(associacao);
            pedido.getStatusPedidoState().enviarParaEntrega();
            pedido.setEntregadorId(associacao.getEntregador().getId());

            cliente.notificaPedidoEmRota(new PedidoEmRotaEvent(pedido, associacao.getEntregador()));
        }

        pedidoRepository.save(pedido);
    }

    @Override
    public PedidoResponseDTO confirmarRecebimento(Long id, Long clienteId, String codigoAcessoCliente) {
        //valida cliente e pedido
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntidadeNaoExisteException("cliente"));
        codigoAcessoValidator.validar(codigoAcessoCliente, cliente.getCodigoAcesso());
        pedidoValidator.validaIdentificador(pedido.getClienteId(), clienteId, "cliente");

        pedido.getStatusPedidoState().confirmarEntrega();

        Estabelecimento estabelecimento = estabelecimentoRepository.findById(pedido.getEstabelecimentoId()).orElseThrow(() -> new EntidadeNaoExisteException("estabelecimento"));
        estabelecimento.notificaPedidoEntregue(new PedidoEntregueEvent(pedido));
        pedidoRepository.save(pedido);

        Associacao associacao = associacaoRepository.findByEntregadorIdAndEstabelecimentoId(pedido.getEntregadorId(), pedido.getEstabelecimentoId());
        associacao.setUltimaEntrega(LocalDateTime.now());
        associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.ATIVO);
        associacaoRepository.save(associacao);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    public void cancelarPedidoCliente(Long id, String codigoAcesso, Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(() -> new EntidadeNaoExisteException("pedido"));
        pedidoValidator.validaIdentificador(pedido.getClienteId(), id, "cliente");

        List<StatusPedido> statusPermitidos = Arrays.asList(StatusPedido.PEDIDO_RECEBIDO, StatusPedido.PEDIDO_EM_PREPARO);
        if (!statusPermitidos.contains(pedido.getStatusPedido())) {
            throw new OperacaoInvalidaException("O pedido nao pode mais ser cancelado", false);
        }
        pedidoRepository.deleteById(pedido.getId());
    }

    private double calculaValorPedido(Pedido pedido) {
        double total = 0.0;
        for (Pizza pizza : pedido.getPizzas())
            total += pizzaService.calcularPreco(pizza);
        return total;
    }

    private void setNovoPedido(Pedido pedido, Long clienteId, Long estabelecimentoId, List<Pizza> pizzas) {
        pedido.setPizzas(pizzas);
        double valorTotal = calculaValorPedido(pedido);
        pedido.setValorPedido(valorTotal);
        pedido.setClienteId(clienteId);
        pedido.setEstabelecimentoId(estabelecimentoId);
        pedido.setStatusPagamento(false);
    }

    private List<PedidoResponseDTO> mapeiaPedidos(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                .toList();
    }
}
