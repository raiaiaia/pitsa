package com.ufcg.psoft.pitsa.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.pitsa.dto.pizza.PizzaRequestDTO;
import com.ufcg.psoft.pitsa.exception.CustomErrorType;
import com.ufcg.psoft.pitsa.model.*;
import com.ufcg.psoft.pitsa.model.enums.*;
import com.ufcg.psoft.pitsa.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de pedidos")
public class PedidoControllerTests {

    final String URI_PEDIDOS = "/pedido";

    @Autowired
    MockMvc driver;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    PizzaRepository pizzaRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    AssociacaoRepository associacaoRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Cliente cliente;
    Entregador entregador;
    Entregador entregadorSemAssociacao;
    Sabor sabor1;
    Sabor sabor2;
    Pizza pizzaM1;
    Pizza pizzaG1;
    Pizza pizzaM2;
    Pizza pizzaG2;
    Estabelecimento estabelecimento;
    Pedido pedido;
    Pedido pedido2;
    PedidoRequestDTO pedidoRequestDTO;
    PedidoRequestDTO pedidoRequestDTO2;
    PedidoRequestDTO pedidoEnderecoNuloDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        sabor1 = Sabor.builder()
                .nome("Sabor Um")
                .tipo(TipoSabor.SALGADO)
                .precoMedia(10.0)
                .precoGrande(20.0)
                .disponivel(true)
                .build();

        sabor2 = Sabor.builder()
                .nome("Sabor Dois")
                .tipo(TipoSabor.DOCE)
                .precoMedia(15.0)
                .precoGrande(30.0)
                .disponivel(true)
                .build();

        List<Sabor> sabores = List.of(sabor1, sabor2);

        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .sabores(sabores)
                .codigoAcesso("111111")
                .build());

        cliente = clienteRepository.save(Cliente.builder()
                .nome("Jose Esoj")
                .endereco("Campina Grande")
                .codigoAcesso("111111")
                .build());

        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Joaozinho")
                .veiculo(new Veiculo("SLD-1B54", TipoVeiculo.MOTO, "Azul"))
                .codigoAcesso("101010")
                .build());

        pizzaM1 = Pizza.builder()
                .sabor1(sabor1)
                .tamanho(TamanhoPizza.MEDIA)
                .build();

        pizzaG1 = Pizza.builder()
                .sabor1(sabor1)
                .sabor2(sabor2)
                .tamanho(TamanhoPizza.GRANDE)
                .build();

        pizzaM2 = Pizza.builder()
                .sabor1(sabor1)
                .tamanho(TamanhoPizza.MEDIA)
                .build();

        pizzaG2 = Pizza.builder()
                .sabor1(sabor1)
                .sabor2(sabor2)
                .tamanho(TamanhoPizza.GRANDE)
                .build();

        List<Pizza> pizzas1 = List.of(pizzaM1, pizzaG1);
        List<Pizza> pizzas2 = List.of(pizzaM2, pizzaG2);

        pedido = Pedido.builder()
                .valorPedido(35.0)
                .clienteId(cliente.getId())
                .estabelecimentoId(estabelecimento.getId())
                .entregadorId(entregador.getId())
                .enderecoEntrega("Rua Jonas, 99")
                .pizzas(pizzas1)
                .statusPedido(StatusPedido.PEDIDO_EM_PREPARO)
                .statusPagamento(true)
                .dataCriacao(LocalDateTime.now())
                .build();

        pedido2 = Pedido.builder()
                .valorPedido(100.0)
                .clienteId(cliente.getId())
                .estabelecimentoId(estabelecimento.getId())
                .entregadorId(entregador.getId())
                .enderecoEntrega("Rua das Castanholas, 99")
                .pizzas(pizzas2)
                .statusPedido(StatusPedido.PEDIDO_EM_PREPARO)
                .statusPagamento(true)
                .dataCriacao(LocalDateTime.now())
                .build();

        List<PizzaRequestDTO> pizzasDTO = pedido.getPizzas().stream()
                .map(pizza -> PizzaRequestDTO.builder()
                        .tamanho(pizza.getTamanho())
                        .sabor1(pizza.getSabor1().getNome())
                        .sabor2(pizza.getSabor2() != null ? pizza.getSabor2().getNome() : null)
                        .build())
                .toList();

        List<PizzaRequestDTO> pizzasDTO2 = pedido2.getPizzas().stream()
                .map(pizza -> PizzaRequestDTO.builder()
                        .tamanho(pizza.getTamanho())
                        .sabor1(pizza.getSabor1().getNome())
                        .sabor2(pizza.getSabor2() != null ? pizza.getSabor2().getNome() : null)
                        .build())
                .toList();

        pedidoRequestDTO = PedidoRequestDTO.builder()
                .enderecoEntrega(pedido.getEnderecoEntrega())
                .pizzas(pizzasDTO)
                .build();

        pedidoRequestDTO2 = PedidoRequestDTO.builder()
                .enderecoEntrega(pedido2.getEnderecoEntrega())
                .pizzas(pizzasDTO2)
                .build();

        pedidoEnderecoNuloDTO = PedidoRequestDTO.builder()
                .enderecoEntrega(null)
                .pizzas(pizzasDTO)
                .build();
    }

    @AfterEach
    void tearDown() {
        associacaoRepository.deleteAll();
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        entregadorRepository.deleteAll();
        pizzaRepository.deleteAll();
        saborRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class PedidoVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando criamos um novo pedido com dados válidos")
        void testCriarPedidoValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(MockMvcRequestBuilders.post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedidoRequestDTO.getEnderecoEntrega(), resultado.getEnderecoEntrega()),
                    () -> assertEquals(pedidoRequestDTO.getPizzas().get(0).getSabor1(), resultado.getPizzas().get(0).getSabor1().getNome()),
                    () -> assertEquals(pedido.getClienteId(), resultado.getClienteId()),
                    () -> assertEquals(pedido.getEstabelecimentoId(), resultado.getEstabelecimentoId()),
                    () -> assertEquals(pedido.getValorPedido(), resultado.getValorPedido())
            );
        }

        @Test
        @DisplayName("Quando tentamos criar um novo pedido com código de acesso inválido")
        void testCriarPedidoCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(MockMvcRequestBuilders.post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", "999999")
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );

        }

        @Test
        @DisplayName("Quando criamos um pedido com um sabor indisponível")
        void testCriarPedidoComSaborIndisponivel() throws Exception {
            //Arrange
            //Mudando o sabor para indispoivel
            sabor1.setDisponivel(false);
            saborRepository.save(sabor1);

            // Act
            String responseJsonString = driver.perform(MockMvcRequestBuilders.post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O sabor consultado nao esta disponivel no momento!", resultado.getMessage())
            );

        }

        @Test
        @DisplayName("Quando criamos um pedido com um sabor inexistente")
        void testCriarPedidoComSaborInexistente() throws Exception {
            //Arrange
            //Removendo o sabor do banco de dados
            saborRepository.delete(sabor1);

            // Act
            String responseJsonString = driver.perform(MockMvcRequestBuilders.post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("sabor inexistente!", resultado.getMessage())
            );

        }

        @Test
        @DisplayName("Quando criamos um pedido inserindo uma pizza M com 2 sabores")
        void testCriarPedidoPizzaMComDoisSabores() throws Exception {
            //Arrange
            pedidoRequestDTO.getPizzas().get(0).setSabor2(sabor2.getNome());

            // Act
            String responseJsonString = driver.perform(MockMvcRequestBuilders.post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isNotAcceptable())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("A quantidade de sabores solicitada excede o valor maximo!", resultado.getMessage())
            );

        }

        @Test
        @DisplayName("Quando criamos um pedido sem endereço de entrega")
        void testCriarPedidoSemEnderecoEntrega() throws Exception {
            //Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(MockMvcRequestBuilders.post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoEnderecoNuloDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertNotEquals(pedidoEnderecoNuloDTO.getEnderecoEntrega(), resultado.getEnderecoEntrega()),
                    () -> assertEquals(pedidoEnderecoNuloDTO.getPizzas().get(0).getSabor1(), resultado.getPizzas().get(0).getSabor1().getNome()),
                    () -> assertEquals(pedido.getClienteId(), resultado.getClienteId()),
                    () -> assertEquals(pedido.getEstabelecimentoId(), resultado.getEstabelecimentoId()),
                    () -> assertEquals(pedido.getValorPedido(), resultado.getValorPedido())
            );

        }

        @Test
        @DisplayName("Quando atualizamos o pedido com dados válidos")
        void testaAtualizarPedidoValido() throws Exception {
            //Arrange
            pedidoRepository.save(pedido);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            //Assert
            assertAll(
                    () -> assertEquals(pedidoRequestDTO.getEnderecoEntrega(), resultado.getEnderecoEntrega()),
                    () -> assertEquals(pedidoRequestDTO.getPizzas().get(0).getSabor1(), resultado.getPizzas().get(0).getSabor1().getNome()),
                    () -> assertEquals(pedido.getClienteId(), resultado.getClienteId()),
                    () -> assertEquals(pedido.getEstabelecimentoId(), resultado.getEstabelecimentoId()),
                    () -> assertEquals(pedido.getValorPedido(), resultado.getValorPedido())
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar pedido inexistente")
        void testAtualizarPedidoInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("pedido inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar pedido código acesso inválido")
        void testAtualizarPedidoCodigoAcessoClienteInvalido() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recuperamos um pedido do cliente pelo pelo id")
        void testRecuperarPedidoClienteId() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);

            // Act
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + "/cliente/" + cliente.getId())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedidoRequestDTO.getEnderecoEntrega(), resultado.getEnderecoEntrega()),
                    () -> assertEquals(pedidoRequestDTO.getPizzas().get(0).getSabor1(), resultado.getPizzas().get(0).getSabor1().getNome()),
                    () -> assertEquals(pedido.getClienteId(), resultado.getClienteId()),
                    () -> assertEquals(pedido.getEstabelecimentoId(), resultado.getEstabelecimentoId()),
                    () -> assertEquals(pedido.getValorPedido(), resultado.getValorPedido())
            );
        }

        @Test
        @DisplayName("Quando recuperamos um pedido do estabelecimento pelo id")
        void testRecuperarPedidoEstabelecimentoId() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);

            // Act
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + "/estabelecimento/" + estabelecimento.getId())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedidoRequestDTO.getEnderecoEntrega(), resultado.getEnderecoEntrega()),
                    () -> assertEquals(pedidoRequestDTO.getPizzas().get(0).getSabor1(), resultado.getPizzas().get(0).getSabor1().getNome()),
                    () -> assertEquals(pedido.getClienteId(), resultado.getClienteId()),
                    () -> assertEquals(pedido.getEstabelecimentoId(), resultado.getEstabelecimentoId()),
                    () -> assertEquals(pedido.getValorPedido(), resultado.getValorPedido())
            );
        }

        @Test
        @DisplayName("Quando listamos todos os pedidos do cliente")
        void testlistarPedidos() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);
            pedidoRepository.save(pedido2);

            // Act
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/cliente/" + cliente.getId() + "/all")
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO2)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Quando listamos todos os pedidos do estabelecimento")
        void testListarPedidosEstabelecimento() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);
            pedidoRepository.save(pedido2);

            // Act
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/estabelecimento/" + estabelecimento.getId() + "/all")
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Quando um cliente tenta recuperar um pedido inexistente")
        void testRecuperaPedidoInexistenteCliente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/999999" + "/cliente/" + cliente.getId())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("pedido inexistente!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um estabelecimento tenta recuperar um pedido inexistente")
        void testRecuperaPedidoInexistenteEstabelecimento() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/999999" + "/estabelecimento/" + estabelecimento.getId())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("pedido inexistente!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente tenta recuperar um pedido de outro cliente")
        void testRecuperarPedidoOutroCliente() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);
            Cliente cliente2 = clienteRepository.save(
                    Cliente.builder()
                            .nome("raiaiaia")
                            .endereco("descubra")
                            .codigoAcesso("123456")
                            .build()
            );

            // Act
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + "/cliente/" + cliente2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoCliente", cliente2.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O id informado para o cliente eh invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando estabelecimento tenta recuperar um pedido de outro estabelecimento")
        void testRecuperarPedidoOutroEstabelecimento() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);

            Sabor saborTest1 = Sabor.builder()
                    .nome("Sabor Um")
                    .tipo(TipoSabor.SALGADO)
                    .precoMedia(10.0)
                    .precoGrande(20.0)
                    .disponivel(true)
                    .build();

            Sabor saborTest2 = Sabor.builder()
                    .nome("Sabor Dois")
                    .tipo(TipoSabor.DOCE)
                    .precoMedia(15.0)
                    .precoGrande(30.0)
                    .disponivel(true)
                    .build();

            List<Sabor> sabores2 = List.of(saborTest1, saborTest2);
            Estabelecimento estabelecimento2 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .sabores(sabores2)
                    .codigoAcesso("222222")
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + "/estabelecimento/" + estabelecimento2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoEstabelecimento", estabelecimento2.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O id informado para o estabelecimento eh invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente remove um pedido valido")
        void testRemoverPedidoValidoCliente() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);

            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando cliente tenta remover pedido inexistente")
        void testRemoverPedidoInexistenteCliente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/999999" + "/cliente/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("pedido inexistente!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente tenta remover pedido de outro cliente")
        void testRemoverPedidoOutroCliente() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);
            Cliente cliente2 = clienteRepository.save(
                    Cliente.builder()
                            .nome("raiaiaia")
                            .endereco("descubra")
                            .codigoAcesso("123456")
                            .build()
            );

            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente/" + cliente2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoCliente", cliente2.getCodigoAcesso()))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O id informado para o cliente eh invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando estabelecimento remove pedido válido")
        void testRemoverPedidoValidoEstabelecimento() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);

            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/estabelecimento/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando cliente tenta remover pedido inexistente")
        void testRemoverPedidoInexistenteEstabelecimento() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/999999" + "/estabelecimento/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("pedido inexistente!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente tenta remover pedido de outro estabelecimento")
        void testRemoverPedidoOutroEstabelecimento() throws Exception {
            // Arrange
            pedidoRepository.save(pedido);
            Sabor saborTest1 = Sabor.builder()
                    .nome("Sabor Um")
                    .tipo(TipoSabor.SALGADO)
                    .precoMedia(10.0)
                    .precoGrande(20.0)
                    .disponivel(true)
                    .build();

            Sabor saborTest2 = Sabor.builder()
                    .nome("Sabor Dois")
                    .tipo(TipoSabor.DOCE)
                    .precoMedia(15.0)
                    .precoGrande(30.0)
                    .disponivel(true)
                    .build();

            List<Sabor> sabores2 = List.of(saborTest1, saborTest2);
            Estabelecimento estabelecimento2 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .sabores(sabores2)
                    .codigoAcesso("222222")
                    .build());

            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/estabelecimento/" + estabelecimento2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoEstabelecimento", estabelecimento2.getCodigoAcesso()))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O id informado para o estabelecimento eh invalido!", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de confirmação de pagamento")
    class PedidoVerificacaoConfirmarPagamento {

        Pedido pedido1;

        @BeforeEach
        void setUp() {
            pedido1 = pedidoRepository.save(Pedido.builder()
                    .estabelecimentoId(estabelecimento.getId())
                    .clienteId(cliente.getId())
                    .enderecoEntrega("Esperança")
                    .pizzas(List.of(pizzaG1))
                    .valorPedido(100.0)
                    .statusPagamento(false)
                    .build()
            );
        }

        @Test
        @DisplayName("Quando confirmamos o pagamento com cartão de crédito")
        void testConfirmarPagamentoCredito() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.CREDITO.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            assertAll(
                    () -> assertEquals(100.0, resultado.getValorPedido()),
                    () -> assertTrue(resultado.isStatusPagamento())
            );

        }

        @Test
        @DisplayName("Quando confirmamos o pagamento com cartão de débito")
        void testConfirmarPagamentoDebito() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.DEBITO.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            assertAll(
                    () -> assertEquals(97.5, resultado.getValorPedido()),
                    () -> assertTrue(resultado.isStatusPagamento())
            );

        }

        @Test
        @DisplayName("Quando confirmamos o pagamento com pix")
        void testConfirmarPagamentoPix() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.PIX.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            assertAll(
                    () -> assertEquals(95.0, resultado.getValorPedido()),
                    () -> assertTrue(resultado.isStatusPagamento())
            );
        }

        @Test
        @DisplayName("Quando confirmamos o pagamento com metodo invalido")
        void testConfirmarPagamentoMetodoInvalido() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", "Fiado")
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Valor invalido para enum de metodoPagamento", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando confirmamos o pagamento passando codigo de acesso inválido")
        void testConfirmarPagamentoCodigoDeAcessoInvalido() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", "9999")
                            .param("metodoPagamento", MetodoPagamento.PIX.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando confirmamos o pagamento com método de pagamento inválido")
        void testConfirmarPagamentoMetodoPagamentoInvalido() throws Exception {
            // Arrange
            String metodoPagamentoInvalido = "DINHEIRO";

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", metodoPagamentoInvalido)
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Valor invalido para enum de metodoPagamento", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando confirmamos o pagamento de pedido já pago")
        void testConfirmarPagamentoPedidoPago() throws Exception {
            // Arrange
            pedido1.setStatusPagamento(true);
            pedidoRepository.saveAndFlush(pedido1);

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.PIX.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pagamento nao pode ser alterado!", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de mudanças nos status dos pedidos")
    class PedidoVerificacaoMudancaStatusPedido {

        Pedido pedido1;
        Associacao associacao1;
        Associacao associacaoe1;
        Associacao associacaoe2;
        Entregador e1;
        Entregador e2;
        private ByteArrayOutputStream logOutputStream;
        private PrintStream originalOut;

        @BeforeEach
        void setUp() {
            logOutputStream = new ByteArrayOutputStream();
            originalOut = System.out;
            System.setOut(new PrintStream(logOutputStream));

            // Object Mapper suporte para LocalDateTime
            objectMapper.registerModule(new JavaTimeModule());

            pedido1 = pedidoRepository.save(Pedido.builder()
                    .estabelecimentoId(estabelecimento.getId())
                    .clienteId(cliente.getId())
                    .enderecoEntrega("Esperança")
                    .pizzas(List.of(pizzaG1))
                    .valorPedido(100.0)
                    .statusPagamento(false)
                    .build()
            );

            e1 = entregadorRepository.save(Entregador.builder()
                    .nome("e1")
                    .veiculo(new Veiculo("SLD-1B55", TipoVeiculo.CARRO, "Preto"))
                    .codigoAcesso("111111")
                    .build());

            e2 = entregadorRepository.save(Entregador.builder()
                    .nome("e2")
                    .veiculo(new Veiculo("SLD-1B55", TipoVeiculo.CARRO, "Preto"))
                    .codigoAcesso("111111")
                    .build());

            associacao1 = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .build()
            );

            associacaoe2 = associacaoRepository.save(Associacao.builder()
                    .entregador(e2)
                    .estabelecimento(estabelecimento)
                    .build()
            );

            associacaoe1 = associacaoRepository.save(Associacao.builder()
                    .entregador(e1)
                    .estabelecimento(estabelecimento)
                    .build()
            );

            entregadorSemAssociacao = entregadorRepository.save(Entregador.builder()
                    .nome("Joana")
                    .veiculo(new Veiculo("SLD-1B55", TipoVeiculo.CARRO, "Preto"))
                    .codigoAcesso("111111")
                    .build());
        }

        @AfterEach
        void tearDown() {
            System.setOut(originalOut);
            associacaoRepository.deleteAll();
            entregadorRepository.deleteAll();
            pedidoRepository.deleteAll();
        }

        // --------------------------- PEDIDO_RECEBIDO ---------------------------

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_RECEBIDO para PEDIDO_EM_PREPARO")
        void testMudarStatusRecebidoParaEmPreparo() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_RECEBIDO);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.CREDITO.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            assertEquals(StatusPedido.PEDIDO_EM_PREPARO, resultado.getStatusPedido());
        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_RECEBIDO para PEDIDO_PRONTO")
        void testMudarStatusRecebidoParaPronto() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_RECEBIDO);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/finalizar-preparo-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido Recebido ainda nao foi pago portanto nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos mudar o status do pedido de PEDIDO_RECEBIDO para PEDIDO_ENTREGUE")
        void testMudarStatusRecebidoParaEntregue() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_RECEBIDO);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-entrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido Recebido ainda nao foi pago portanto nao pode ser alterado!", resultado.getMessage());
        }

        // --------------------------- PEDIDO_EM_PREPARO ---------------------------

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_EM_PREPARO para PEDIDO_PRONTO")
        void testMudarStatusEmPreparoParaPronto() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_EM_PREPARO);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/finalizar-preparo-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            assertEquals(StatusPedido.PEDIDO_PRONTO, resultado.getStatusPedido());
        }


        @Test
        @DisplayName("Quando finalizamos pedido EM_PREPARO e tem entregadores disponiveis")
        void testFinalizarPedidoComEntregadoresDisponiveis() throws Exception {
            // Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_EM_PREPARO);
            associacaoe1.setStatus(StatusAssociacao.APROVADO);
            associacaoe2.setStatus(StatusAssociacao.APROVADO);

            associacaoe1.setDisponibilidadeEntregador(DisponibilidadeEntregador.ATIVO);
            associacaoe1.setUltimaEntrega(LocalDateTime.of(2023, 3, 28, 14, 33, 48, 640000));

            associacaoe2.setDisponibilidadeEntregador(DisponibilidadeEntregador.ATIVO);
            associacaoe2.setUltimaEntrega(LocalDateTime.of(2024, 3, 28, 14, 33, 48, 640000));

            pedidoRepository.save(pedido1);
            associacaoRepository.save(associacaoe1);
            associacaoRepository.save(associacaoe2);

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/finalizar-preparo-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("entregadorId", entregador.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);
            String logContent = logOutputStream.toString();

            assertAll(
                    // pedido em rota
                    () -> assertEquals(StatusPedido.PEDIDO_EM_ROTA, resultado.getStatusPedido()),
                    // entregador com prioridade entregando
                    () -> assertEquals(resultado.getEntregadorId(), e1.getId()),
                    //entregador em status entregando
                    () -> assertEquals(associacaoRepository.findByEntregadorIdAndEstabelecimentoId(resultado.getEntregadorId(), associacao1.getEstabelecimento().getId()).getDisponibilidadeEntregador(), DisponibilidadeEntregador.ENTREGANDO),
                    //cliente notificado
                    () -> assertTrue(logContent.contains(cliente.getNome() + ", o seu pedido " + pedido1.getId() + " saiu para entrega!"))
            );

        }

        @Test
        @DisplayName("Quando finalizamos pedido EM_PREPARO e NAO tem entregadores disponiveis")
        void testFinalizarPedidoSemEntregadoresDisponiveis() throws Exception {
            // Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_EM_PREPARO);
            associacaoe1.setStatus(StatusAssociacao.APROVADO);
            associacaoe2.setStatus(StatusAssociacao.APROVADO);

            associacaoe1.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_DESCANSO);
            associacaoe1.setUltimaEntrega(LocalDateTime.of(2023, 3, 28, 14, 33, 48, 640000));

            associacaoe2.setDisponibilidadeEntregador(DisponibilidadeEntregador.ENTREGANDO);
            associacaoe2.setUltimaEntrega(LocalDateTime.of(2024, 3, 28, 14, 33, 48, 640000));

            pedidoRepository.save(pedido1);
            associacaoRepository.save(associacaoe1);
            associacaoRepository.save(associacaoe2);

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/finalizar-preparo-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("entregadorId", entregador.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);
            String logContent = logOutputStream.toString();

            assertAll(
                    // pedido pronto
                    () -> assertEquals(StatusPedido.PEDIDO_PRONTO, resultado.getStatusPedido()),
                    //cliente notificado
                    () -> assertTrue(logContent.contains("Cliente " + cliente.getNome() + ", os entregadores estao indisponiveis, por favor aguarde ate que alguem possa trazer seu pedido " + pedido1.getId()))
            );

        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_EM_PREPARO para PEDIDO_EM_PREPARO")
        void testMudarStatusEmPreparoParaEmPreparo() throws Exception {
            // Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_EM_PREPARO);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.CREDITO.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ja esta Em Preparo portanto nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_EM_PREPARO para PEDIDO_ENTREGUE")
        void testMudarStatusEmPreparoParaEntregue() throws Exception {
            // Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_EM_PREPARO);
            pedidoRepository.save(pedido1);

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-entrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ainda esta Em Preparo portanto nao pode ser alterado!", resultado.getMessage());
        }

        // --------------------------- PEDIDO_PRONTO ---------------------------

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_PRONTO para PEDIDO_EM_PREPARO")
        void testMudarStatusProntoParaEmPreparo() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_PRONTO);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.CREDITO.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ja esta Pronto portanto nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_PRONTO para PEDIDO_PRONTO")
        void testMudarStatusProntoParaPronto() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_PRONTO);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/finalizar-preparo-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ja esta Pronto portanto nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_PRONTO para PEDIDO_ENTREGUE")
        void testMudarStatusProntoParaEntregue() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_PRONTO);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-entrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido Pronto ainda nao foi atribuido a um entregador portanto nao pode ser alterado!", resultado.getMessage());
        }

        // --------------------------- PEDIDO_EM_ROTA ---------------------------

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_EM_ROTA para PEDIDO_ENTREGUE")
        void testMudarStatusEmRotaParaEntregue() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_EM_ROTA);
            pedido1.setStatusPagamento(true);
            associacaoRepository.save(associacao1);
            pedido1.setEntregadorId(entregador.getId());
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-entrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            String logContent = logOutputStream.toString();
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            //Assert
            assertAll(
                    () -> assertFalse(logContent.contains("Pedido " + pedido1.getId() + " foi entregue com sucesso")),
                    () -> assertEquals(StatusPedido.PEDIDO_ENTREGUE, resultado.getStatusPedido()),
                    () -> assertEquals(DisponibilidadeEntregador.ATIVO, associacaoRepository.findByEntregadorIdAndEstabelecimentoId(resultado.getEntregadorId(), associacao1.getEstabelecimento().getId()).getDisponibilidadeEntregador())
            );
        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_EM_ROTA para PEDIDO_EM_PREPARO")
        void testMudarStatusEmRotaParaEmPreparo() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_EM_ROTA);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.CREDITO.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ja esta Em Rota portanto nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_EM_ROTA para PEDIDO_PRONTO")
        void testMudarStatusEmRotaParaPronto() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_EM_ROTA);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/finalizar-preparo-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ja esta Em Rota portanto nao pode ser alterado!", resultado.getMessage());
        }

        // --------------------------- PEDIDO_ENTREGUE ---------------------------

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_ENTREGUE para PEDIDO_EM_PREPARO")
        void testMudarStatusEntregueParaEmPreparo() throws Exception {

            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_ENTREGUE);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("metodoPagamento", MetodoPagamento.CREDITO.toString())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ja foi Entregue portanto nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_ENTREGUE para PEDIDO_PRONTO")
        void testMudarStatusEntregueParaPronto() throws Exception {
            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_ENTREGUE);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/finalizar-preparo-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ja foi Entregue portanto nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando mudamos o status do pedido de PEDIDO_ENTREGUE para PEDIDO_ENTREGUE")
        void testMudarStatusEntregueParaEntregue() throws Exception {

            //Arrange
            pedido1.setStatusPedido(StatusPedido.PEDIDO_ENTREGUE);
            pedidoRepository.save(pedido1);

            //Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId() + "/confirmar-entrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido ja foi Entregue portanto nao pode ser alterado!", resultado.getMessage());
        }
    }
}