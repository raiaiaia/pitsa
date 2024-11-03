package com.ufcg.psoft.pitsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.pitsa.dto.associacao.AssociacaoResponseDTO;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Associacao")
public class AssociacaoControllerTests {

    final String URI_ASSOCIACAO = "/associacao";

    @Autowired
    MockMvc driver;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    PizzaRepository pizzaRepository;

    @Autowired
    SaborRepository saborRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Entregador entregador;
    Veiculo veiculo;
    Estabelecimento estabelecimento;
    Associacao associacao;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        veiculo = new Veiculo("SLD-1B54", TipoVeiculo.CARRO, "Azul");
        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Entregador Um da Silva")
                .veiculo(veiculo)
                .codigoAcesso("123456")
                .build()
        );
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .codigoAcesso("122110")
                .sabores(new ArrayList<>())
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        associacaoRepository.deleteAll();
        entregadorRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        pizzaRepository.deleteAll();
        saborRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de criacao de associacao")
    class AssociacaoVerificacaoCriacao {

        @Test
        @DisplayName("Quando criamos uma associação válida")
        void testCriarAssociacaoValido() throws Exception {

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.AssociacaoResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    resultado::getId,
                    () -> assertEquals(entregador.getId(), resultado.getEntregador().getId()),
                    () -> assertEquals(estabelecimento.getId(), resultado.getEstabelecimento().getId()),
                    () -> assertEquals(StatusAssociacao.EM_ANALISE, resultado.getStatus())
            );
        }

        @Test
        @DisplayName("Quando tentamos criar uma associação com entregador inexistente")
        void testCriarAssociacaoEntregadorInexistente() throws Exception {

            //Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", "9999")
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("entregador inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos criar uma associação passando codigo de acesso inválido")
        void testCriarAssociacaoCodigoDeAcessoInvalido() throws Exception {

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcessoEntregador", "9999")
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos criar uma associação com estabelecimento inexistente")
        void testCriarAssociacaoEstabelecimentoInexistente() throws Exception {

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", "9999"))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }
    }


    @Nested
    @DisplayName("Conjunto de casos de verificação da aprovação de entregadores")
    class AssociacaoVerificacaoAprovacao {

        @BeforeEach
        void setup() {
            // Object Mapper suporte para LocalDateTime
            objectMapper.registerModule(new JavaTimeModule());

            associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .build()
            );
        }

        @Test
        @DisplayName("Quando aprovamos um entregador em analise")
        void testAprovarEntregadorEmAnalise() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isOk()) //Código 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.AssociacaoResponseDTOBuilder.class).build();

            // Assert
            assertEquals("APROVADO", resultado.getStatus().toString());
        }

        @Test
        @DisplayName("Quando tentamos aprovar um entregador já rejeitado")
        void testAprovarEntregadorRejeitado() throws Exception {
            //Arrange
            associacaoRepository.deleteAll();
            Associacao associacaoRejeitada = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .status(StatusAssociacao.REJEITADO)
                    .build()
            );

            associacaoRejeitada.setStatus(StatusAssociacao.APROVADO);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isBadRequest()) //Código 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("O status nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos aprovar um entregador já aprovado")
        void testAprovarEntregadorAprovado() throws Exception {
            //Arrange
            associacaoRepository.deleteAll();
            Associacao associacaoAprovada = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .status(StatusAssociacao.APROVADO)
                    .build()
            );

            associacaoAprovada.setStatus(StatusAssociacao.APROVADO);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isBadRequest()) //Código 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("O status nao pode ser alterado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos aprovar um entregador com estabelecimento inexistente")
        void testAprovarEntregadorEstabelecimentoInexistente() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);

            Long estabelecimentoIdInvalido = 990L;

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimentoIdInvalido.toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isNotFound()) //Código 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos aprovar um entregador inexistente")
        void testAprovarEntregadorInexistente() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);

            Long entregadorIdInvalido = 990L;

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregadorIdInvalido.toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isNotFound()) //Código 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("entregador inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos aprovar um entregador com o codigo de acesso do estabelecimento inválido")
        void testAprovarEntregadorCodigoAcessoInvalido() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);

            String codigoAcessoInvalido = "121212";

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", codigoAcessoInvalido)
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isUnauthorized()) //Código 401
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de rejeição de entregadores")
    class AssociacaoVerificacaoRejeicao {

        @BeforeEach
        void setup() {
            // Object Mapper suporte para LocalDateTime
            objectMapper.registerModule(new JavaTimeModule());

            associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .build()
            );
        }

        @Test
        @DisplayName("Quando rejeitamos um entregador em analise")
        void testRejeitarEntregadorEmAnalise() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.REJEITADO);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isOk()) //Código 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.AssociacaoResponseDTOBuilder.class).build();

            // Assert
            assertEquals("REJEITADO", resultado.getStatus().toString());
        }

        @Test
        @DisplayName("Quando tentamos rejeitar um entregador com estabelecimento inexistente")
        void testRejeitarEntregadorEstabelecimentoInexistente() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.REJEITADO);

            Long estabelecimentoIdInvalido = 990L;

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimentoIdInvalido.toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isNotFound()) //Código 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos rejeitar um entregador inexistente")
        void testRejeitarEntregadorInexistente() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.REJEITADO);

            Long entregadorIdInvalido = 990L;

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregadorIdInvalido.toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isNotFound()) //Código 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("entregador inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos rejeitar um entregador com o codigo de acesso do estabelecimento inválido")
        void testRejeitarEntregadorCodigoAcessoInvalido() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.REJEITADO);

            String codigoAcessoInvalido = "121212";

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", codigoAcessoInvalido)
                            .param("statusAssociacao", associacao.getStatus().toString()))
                    .andExpect(status().isUnauthorized()) //Código 401
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da alteração de associação")
    class AssociacaoVerificacaoAtualizacao {

        @BeforeEach
        void setup() {
            // Object Mapper suporte para LocalDateTime
            objectMapper.registerModule(new JavaTimeModule());

            associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .build()
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar uma associação com status inválido")
        void testAlterarStatusAssociacaoInvalido() throws Exception {
            //Arrange
            String statusInvalido = "DESLIGADO";

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", statusInvalido))
                    .andExpect(status().isBadRequest()) //Código 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Valor invalido para enum de statusAssociacao", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar uma associação não cadastrada")
        void testAlterarAssociacaoInexistente() throws Exception {
            //Arrange
            Veiculo veiculo2 = new Veiculo("RNX-1B54", TipoVeiculo.MOTO, "Preto");
            Entregador entregador2 = entregadorRepository.save(Entregador.builder()
                    .nome("Entregador Dois da Silva")
                    .veiculo(veiculo2)
                    .codigoAcesso("010101")
                    .build()
            );

            Estabelecimento estabelecimento2 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("000001")
                    .build()
            );

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador2.getId().toString())
                            .param("estabelecimentoId", estabelecimento2.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento2.getCodigoAcesso())
                            .param("statusAssociacao", StatusAssociacao.APROVADO.toString()))
                    .andExpect(status().isNotFound()) //Código 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("associacao inexistente!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de re-associação")
    class AssociacaoVerificacaoOutraTentativa {

        @BeforeEach
        void setup() {
            // Object Mapper suporte para LocalDateTime
            objectMapper.registerModule(new JavaTimeModule());

            associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .build()
            );
        }

        @Test
        @DisplayName("Quando tentamos associar um entregador que ja foi rejeitado pelo estabelecimento")
        void testCriarOutraAssociacaoEntregadorRejeitado() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.REJEITADO);
            associacaoRepository.save(associacao);

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.AssociacaoResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(2, associacaoRepository.count()),
                    resultado::getId,
                    () -> assertEquals(entregador.getId(), resultado.getEntregador().getId()),
                    () -> assertEquals(estabelecimento.getId(), resultado.getEstabelecimento().getId()),
                    () -> assertEquals(StatusAssociacao.EM_ANALISE, resultado.getStatus())
            );
        }

        @Test
        @DisplayName("Quando tentamos associar um entregador que ja foi aprovado pelo estabelecimento")
        void testCriarOutraAssociacaoEntregadorAprovado() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);
            associacaoRepository.save(associacao);

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isUnprocessableEntity()) //Codigo 422
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Associacao ja existe com entregador Entregador Um da Silva para esse estabelecimento com status APROVADO", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos associar um entregador que ainda nao foi analisado pelo estabelecimento")
        void testCriarOutraAssociacaoEntregadorEmAnalise() throws Exception {
            //Arrange
            // nada alem do setup()

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isUnprocessableEntity()) //Codigo 422
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Associacao ja existe com entregador Entregador Um da Silva para esse estabelecimento com status EM_ANALISE", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da alteração da disponibilidade do entregador")
    class AssociacaoVerificacaoAtualizacaoDisponibilidadeEntregador {

        Pedido pedido;
        Cliente cliente;
        Pizza pizzaG1;
        Sabor sabor1;
        Sabor sabor2;

        @BeforeEach
        void setup() {
            // Object Mapper suporte para LocalDateTime
            objectMapper.registerModule(new JavaTimeModule());

            cliente = clienteRepository.save(Cliente.builder()
                    .nome("Jose Esoj")
                    .endereco("Campina Grande")
                    .codigoAcesso("111111")
                    .build());

            sabor1 = saborRepository.save(Sabor.builder()
                    .nome("Sabor Um")
                    .tipo(TipoSabor.SALGADO)
                    .precoMedia(10.0)
                    .precoGrande(20.0)
                    .disponivel(true)
                    .build()
            );

            sabor2 = saborRepository.save(Sabor.builder()
                    .nome("Sabor Dois")
                    .tipo(TipoSabor.DOCE)
                    .precoMedia(15.0)
                    .precoGrande(30.0)
                    .disponivel(true)
                    .build()
            );

            pizzaG1 = Pizza.builder()
                    .sabor1(sabor1)
                    .sabor2(sabor2)
                    .tamanho(TamanhoPizza.GRANDE)
                    .build();

            pedido = pedidoRepository.save(Pedido.builder()
                    .estabelecimentoId(estabelecimento.getId())
                    .clienteId(cliente.getId())
                    .enderecoEntrega("Esperança")
                    .pizzas(List.of(pizzaG1))
                    .valorPedido(100.0)
                    .statusPagamento(true)
                    .build()
            );

            associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .build()
            );
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade de um entregador aprovado para em atividade e não há pedidos prontos")
        void testAlterarDisponibilidadeEntregadorAprovadoSemPedidosProntos() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);
            associacaoRepository.save(associacao);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/atualizar-disponibilidade-entregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("associacaoId", associacao.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("disponibilidadeEntregador", DisponibilidadeEntregador.ATIVO.toString()))
                    .andExpect(status().isOk()) //Código 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.AssociacaoResponseDTOBuilder.class).build();

            // Assert
            assertEquals("ATIVO", resultado.getDisponibilidadeEntregador().toString());
        }


        @Test
        @DisplayName("Quando alteramos a disponibilidade de um entregador aprovado para em atividade e há pedidos prontos")
        void testAlterarDisponibilidadeEntregadorAprovadoComPedidosProntos() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);
            associacaoRepository.save(associacao);
            pedido.setStatusPedido(StatusPedido.PEDIDO_PRONTO);
            pedidoRepository.save(pedido);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/atualizar-disponibilidade-entregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("associacaoId", associacao.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("disponibilidadeEntregador", DisponibilidadeEntregador.ATIVO.toString()))
                    .andExpect(status().isOk()) //Código 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.AssociacaoResponseDTOBuilder.class).build();

            // Assert
            assertEquals(DisponibilidadeEntregador.ENTREGANDO, resultado.getDisponibilidadeEntregador());
        }

        @Test
        @DisplayName("Quando aprovamos um entregador sua disponibilidade deve ser em descanso")
        void testAprovarEntregadorComDisponibilidadePadrao() throws Exception {
            //Arrange

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("statusAssociacao", StatusAssociacao.APROVADO.toString()))
                    .andExpect(status().isOk()) //Código 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.AssociacaoResponseDTOBuilder.class).build();

            // Assert
            assertEquals("EM_DESCANSO", resultado.getDisponibilidadeEntregador().toString());
        }

        @Test
        @DisplayName("Quando tentamos mudar disponibilidade de um entregador rejeitado")
        void testAlterarDisponibilidadeEntregadorRejeitado() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.REJEITADO);
            associacaoRepository.save(associacao);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/atualizar-disponibilidade-entregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("associacaoId", associacao.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("disponibilidadeEntregador", DisponibilidadeEntregador.ATIVO.toString()))
                    .andExpect(status().isBadRequest()) //Código 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O atributo disponibilidade do entregador nao pode ser alterado!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos mudar disponibilidade de um entregador em analise")
        void testAlterarDisponibilidadeEntregadorEmAnalise() throws Exception {
            //Arrange

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/atualizar-disponibilidade-entregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("associacaoId", associacao.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("disponibilidadeEntregador", DisponibilidadeEntregador.ATIVO.toString()))
                    .andExpect(status().isBadRequest()) //Código 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O atributo disponibilidade do entregador nao pode ser alterado!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos mudar disponibilidade de um entregador de uma associacao invalida")
        void testAlterarDisponibilidadeEntregadorAssociacaoInexistente() throws Exception {
            //Arrange

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/atualizar-disponibilidade-entregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("associacaoId", "12123")
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("disponibilidadeEntregador", DisponibilidadeEntregador.ATIVO.toString()))
                    .andExpect(status().isNotFound()) //Código 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("associacao inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos mudar disponibilidade de um entregador nao cadastrado no sistema")
        void testAlterarDisponibilidadeEntregadorInexistente() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);
            associacaoRepository.save(associacao);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/atualizar-disponibilidade-entregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", "12123")
                            .param("associacaoId", associacao.getId().toString())
                            .param("codigoAcessoEntregador", entregador.getCodigoAcesso())
                            .param("disponibilidadeEntregador", DisponibilidadeEntregador.ATIVO.toString()))
                    .andExpect(status().isNotFound()) //Código 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("entregador inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos mudar disponibilidade de um entregador com codigo de acesso invalido")
        void testAlterarDisponibilidadeEntregadorCodigoAcessoInvalido() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);
            associacaoRepository.save(associacao);

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/atualizar-disponibilidade-entregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("associacaoId", associacao.getId().toString())
                            .param("codigoAcessoEntregador", "12123")
                            .param("disponibilidadeEntregador", DisponibilidadeEntregador.ATIVO.toString()))
                    .andExpect(status().isUnauthorized()) //Código 401
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos mudar o atributo disponibilidade de um entregador para um valor invalido")
        void testAlterarDisponibilidadeEntregadorValorInvalido() throws Exception {
            //Arrange
            associacao.setStatus(StatusAssociacao.APROVADO);
            associacaoRepository.save(associacao);

            String statusInvalido = "DORMINDO";

            //Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/atualizar-disponibilidade-entregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("associacaoId", associacao.getId().toString())
                            .param("codigoAcessoEntregador", "12123")
                            .param("disponibilidadeEntregador", statusInvalido))
                    .andExpect(status().isBadRequest()) //Código 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Valor invalido para enum de disponibilidadeEntregador", resultado.getMessage())
            );
        }
    }
}
