package com.ufcg.psoft.pitsa.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.pitsa.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.pitsa.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.pitsa.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.pitsa.dto.pizza.PizzaRequestDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.pitsa.exception.CustomErrorType;
import com.ufcg.psoft.pitsa.model.*;
import com.ufcg.psoft.pitsa.model.enums.StatusPedido;
import com.ufcg.psoft.pitsa.model.enums.TamanhoPizza;
import com.ufcg.psoft.pitsa.model.enums.TipoSabor;
import com.ufcg.psoft.pitsa.model.enums.TipoVeiculo;
import com.ufcg.psoft.pitsa.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Clientes")
public class ClienteControllerTests {

    final String URI_CLIENTES = "/cliente";

    @Autowired
    MockMvc driver;

    @Autowired
    ClienteRepository clienteRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Cliente cliente;
    ClienteRequestDTO clienteRequestDTO;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco("Rua dos Testes, 123")
                .codigoAcesso("123456")
                .build()
        );
        clienteRequestDTO = ClienteRequestDTO.builder()
                .nome(cliente.getNome())
                .endereco(cliente.getEndereco())
                .codigoAcesso(cliente.getCodigoAcesso())
                .build();
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class ClienteVerificacaoNome {

        @Test
        @DisplayName("Quando tentamos criar um cliente com o nome nulo")
        void testCriarClienteNomeNulo() throws Exception {
            // Arrange
            ClienteRequestDTO clienteNomeNulo = ClienteRequestDTO.builder()
                    .nome(null)
                    .endereco("Rua dos Testes")
                    .codigoAcesso("123456")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteNomeNulo)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos criar um cliente com o nome vazio")
        void testCriarClienteNomeVazio() throws Exception {
            // Arrange
            ClienteRequestDTO clienteNomeEmBranco = ClienteRequestDTO.builder()
                    .nome("")
                    .endereco("Rua dos Testes")
                    .codigoAcesso("123456")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteNomeEmBranco)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente com nome válido")
        void testAtualizarClienteNomeValido() throws Exception {
            // Arrange
            clienteRequestDTO.setNome("Cliente Um Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.ClienteBuilder.class).build();

            // Assert
            assertEquals("Cliente Um Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando tentamos atualizar cliente com nome nulo")
        void testAtualizarClienteNomeNulo() throws Exception {
            // Arrange
            clienteRequestDTO.setNome(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar o cliente com nome vazio")
        void testAtualizarClienteNomeVazio() throws Exception {
            // Arrange
            clienteRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do endereço")
    class ClienteVerificacaoEndereco {

        @Test
        @DisplayName("Quando tentamos criar um cliente com endereço nulo")
        void testCriarClienteEnderecoNulo() throws Exception {
            // Arrange
            ClienteRequestDTO clienteEnderecoNulo = ClienteRequestDTO.builder()
                    .nome("Astrogildo da Silva")
                    .endereco(null)
                    .codigoAcesso("123456")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteEnderecoNulo)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }


        @Test
        @DisplayName("Quando tentamos criar um cliente com endereço vazio")
        void testCriarClienteEnderecoVazio() throws Exception {
            // Arrange
            ClienteRequestDTO clienteEnderecoEmBranco = ClienteRequestDTO.builder()
                    .nome("Astrogildo da Silva")
                    .endereco("")
                    .codigoAcesso("123456")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteEnderecoEmBranco)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente com endereço válido")
        void testAtualizarClienteEnderecoValido() throws Exception {
            // Arrange
            clienteRequestDTO.setEndereco("Endereco Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.ClienteResponseDTOBuilder.class).build();

            // Assert
            assertEquals("Endereco Alterado", resultado.getEndereco());
        }

        @Test
        @DisplayName("Quando tentamos atualizar o cliente com endereço nulo")
        void testAtualizarClienteEnderecoNulo() throws Exception {
            // Arrange
            clienteRequestDTO.setEndereco(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar cliente com endereço vazio")
        void testAtualizarClienteEnderecoVazio() throws Exception {
            // Arrange
            clienteRequestDTO.setEndereco("");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do código de acesso")
    class ClienteVerificacaoCodigoAcesso {

        @Test
        @DisplayName("Quando tentamos atualizar o cliente com código de acesso nulo")
        void testAtualizarClienteCodigoAcessoNulo() throws Exception {
            // Arrange
            clienteRequestDTO.setCodigoAcesso(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar o cliente com código de acesso de mais de 6 digitos")
        void testAtualizarClienteCodigoAcessoMaisDe6Digitos() throws Exception {
            // Arrange
            clienteRequestDTO.setCodigoAcesso("1234567");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar o cliente com código de acesso de menos de 6 digitos")
        void testAtualizarClienteCodigoAcessoMenosDe6Digitos() throws Exception {
            // Arrange
            clienteRequestDTO.setCodigoAcesso("12345");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar o cliente com código de acesso não numérico")
        void testAtualizarClienteCodigoAcessoNaoNumerico() throws Exception {
            // Arrange
            clienteRequestDTO.setCodigoAcesso("a*c4e@");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class ClienteVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando criamos um novo cliente com dados válidos")
        void testCriarClienteValido() throws Exception {
            // Arrange
            ClienteRequestDTO clienteRequestDTO2 = ClienteRequestDTO.builder()
                    .nome("Cliente Um da Silva")
                    .endereco("Rua dos Testes, 123")
                    .codigoAcesso("010101")
                    .build();

            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.ClienteBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clienteRequestDTO2.getNome(), resultado.getNome())
            );

        }

        @Test
        @DisplayName("Quando listamos todos os clientes salvos")
        void testListarClientesSalvos() throws Exception {
            // Arrange
            // Vamos ter 3 clientes no banco
            Cliente cliente1 = Cliente.builder()
                    .nome("Cliente Dois Almeida")
                    .endereco("Av. da Pits A, 100")
                    .codigoAcesso("246810")
                    .build();
            Cliente cliente2 = Cliente.builder()
                    .nome("Cliente Três Lima")
                    .endereco("Distrito dos Testadores, 200")
                    .codigoAcesso("135790")
                    .build();
            clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cliente> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(3, resultado.size())
            );

        }

        @Test
        @DisplayName("Quando recuperamos um cliente salvo pelo id")
        void testRecuperarClienteSalvo() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(cliente.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando tentamos recuperar um cliente inexistente")
        void testRecuperarClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + 999999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando atualizamos o cliente com dados válidos")
        void testAtualizarClienteValido() throws Exception {
            // Arrange
            Long clienteId = cliente.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.ClienteBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getId().longValue(), clienteId),
                    () -> assertEquals(clienteRequestDTO.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar um cliente inexistente")
        void testAtualizarClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + 99999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar o cliente passando código de acesso inválido")
        void testAtualizarClienteCodigoAcessoInvalido() throws Exception {
            // Arrange
            Long clienteId = cliente.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + clienteId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "invalido")
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
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
        @DisplayName("Quando removemos um cliente salvo")
        void testRemoverClienteValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando tentamos remover um cliente inexistente")
        void testRemoverClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos remover um cliente passando código de acesso inválido")
        void testRemoverClienteCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "invalido"))
                    .andExpect(status().isUnauthorized())
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
    @DisplayName("Conjunto de casos de verificação da funcionalidade de demonstrar interesse em um sabor indisponível")
    class ClienteVerificacaoDemonstrarInteresse {

        Sabor s1;
        Sabor s2;
        Estabelecimento estabelecimento1;

        @Autowired
        EstabelecimentoRepository estabelecimentoRepository;

        @Autowired
        SaborRepository saborRepository;

        @BeforeEach
        void setup() {
            s1 = Sabor.builder()
                    .nome("Portuguesa")
                    .precoMedia(20.0)
                    .precoGrande(30.0)
                    .tipo(TipoSabor.SALGADO)
                    .disponivel(false)
                    .build();

            s2 = Sabor.builder()
                    .nome("Marguerita")
                    .precoMedia(22.0)
                    .precoGrande(32.0)
                    .tipo(TipoSabor.SALGADO)
                    .build();

            estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .sabores(List.of(s1, s2))
                    .build();

            estabelecimentoRepository.save(estabelecimento1);

        }

        @AfterEach
        void tearDown() {
            estabelecimentoRepository.deleteAll();
            saborRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando demonstramos interesse em um sabor com dados válidos")
        void testDemonstrarInteresseSaborValido() throws Exception {
            //Arrange
            // nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();
            ArrayList<Long> idsResultado = new ArrayList<>();
            for (Cliente c : resultado.getClientesInteressados()) {
                idsResultado.add(c.getId());
            }

            //Assert
            assertAll(
                    () -> assertFalse(resultado.getDisponivel()),
                    () -> assertEquals(1, resultado.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado.contains(cliente.getId()))
            );

        }

        @Test
        @DisplayName("Quando demonstramos interesse em um sabor que já demonstramos interesse")
        void testDemonstrarInteresseSaborInteressePrevio() throws Exception {
            //Arrange
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            ArrayList<Long> idsResultado = new ArrayList<>();
            for (Cliente c : resultado.getClientesInteressados()) {
                idsResultado.add(c.getId());
            }

            assertAll(
                    () -> assertFalse(resultado.getDisponivel()),
                    () -> assertEquals(1, resultado.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado.contains(cliente.getId()))
            );

            //Act
            String responseJsonString2 = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado2 = objectMapper.readValue(responseJsonString2, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            //Assert
            assertAll(
                    () -> assertFalse(resultado2.getDisponivel()),
                    () -> assertEquals(1, resultado2.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado.contains(cliente.getId()))
            );

        }

        @Test
        @DisplayName("Quando removemos o interesse em um sabor que demonstramos interesse")
        void testRemoverInteresseSabor() throws Exception {
            //Arrange
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();
            ArrayList<Long> idsResultado = new ArrayList<>();
            for (Cliente c : resultado.getClientesInteressados()) {
                idsResultado.add(c.getId());
            }

            assertAll(
                    () -> assertFalse(resultado.getDisponivel()),
                    () -> assertEquals(1, resultado.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado.contains(cliente.getId()))
            );

            //Act
            String responseJsonString2 = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor/remover")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado2 = objectMapper.readValue(responseJsonString2, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertFalse(resultado2.getDisponivel()),
                    () -> assertEquals(0, resultado2.getClientesInteressados().size())

            );
        }

        @Test
        @DisplayName("Quando removemos o interesse em um sabor que não demonstramos interesse")
        void testRemoverInteresseSaborSemInteressePrevio() throws Exception {
            //Arrange
            // nenhuma necessidade além do setup()

            //Act
            String responseJsonString2 = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor/remover")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado2 = objectMapper.readValue(responseJsonString2, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            // não lança erro
            assertAll(
                    () -> assertFalse(resultado2.getDisponivel()),
                    () -> assertEquals(0, resultado2.getClientesInteressados().size())
            );
        }

        @Test
        @DisplayName("Quando removemos o interesse em um sabor inexistente")
        void testRemoverInteresseSaborInexistente() throws Exception {
            //Arrange
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();
            ArrayList<Long> idsResultado = new ArrayList<>();
            for (Cliente c : resultado.getClientesInteressados()) {
                idsResultado.add(c.getId());
            }


            assertAll(
                    () -> assertFalse(resultado.getDisponivel()),
                    () -> assertEquals(1, resultado.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado.contains(cliente.getId()))
            );

            //Act
            String responseJsonString2 = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor/remover")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", "99999999999")
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isNotFound()) // Codigo 401
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado2 = objectMapper.readValue(responseJsonString2, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("sabor inexistente!", resultado2.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos o interesse em um sabor de um cliente inexistente")
        void testRemoverInteresseClienteInexistente() throws Exception {
            //Arrange
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();
            ArrayList<Long> idsResultado = new ArrayList<>();
            for (Cliente c : resultado.getClientesInteressados()) {
                idsResultado.add(c.getId());
            }

            assertAll(
                    () -> assertFalse(resultado.getDisponivel()),
                    () -> assertEquals(1, resultado.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado.contains(cliente.getId()))
            );

            //Act
            String responseJsonString2 = driver.perform(put(URI_CLIENTES + "/99999999/interesse-sabor/remover")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isNotFound()) // Codigo 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado2 = objectMapper.readValue(responseJsonString2, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado2.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos o interesse em um sabor com código de acesso inválido")
        void testRemoverInteresseCodigoAcessoInvalido() throws Exception {
            //Arrange
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();
            ArrayList<Long> idsResultado = new ArrayList<>();
            for (Cliente c : resultado.getClientesInteressados()) {
                idsResultado.add(c.getId());
            }

            assertAll(
                    () -> assertFalse(resultado.getDisponivel()),
                    () -> assertEquals(1, resultado.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado.contains(cliente.getId()))
            );

            //Act
            String responseJsonString2 = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor/remover")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999")
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isUnauthorized()) // Codigo 401
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado2 = objectMapper.readValue(responseJsonString2, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado2.getMessage())
            );
        }

        @Test
        @DisplayName("Quando demonstramos interesse em um sabor disponivel")
        void testDemonstrarInteresseSaborDisponivel() throws Exception {
            //Arrange
            // nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s2.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("Nao eh permitido demonstrar interesse em sabor ja disponivel!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando dois clientes demonstram interesse em um sabor")
        void testDemonstrarInteresseSaborDoisClientes() throws Exception {
            //Arrange
            Cliente c2 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Dois Andrade Pires")
                    .endereco("Rua Bairro")
                    .codigoAcesso("654321")
                    .build()
            );

            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado1 = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();
            ArrayList<Long> idsResultado = new ArrayList<>();
            for (Cliente c : resultado1.getClientesInteressados()) {
                idsResultado.add(c.getId());
            }

            assertAll(
                    () -> assertFalse(resultado1.getDisponivel()),
                    () -> assertEquals(1, resultado1.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado.contains(cliente.getId()))
            );

            // Act (segundo cliente)
            responseJsonString = driver.perform(put(URI_CLIENTES + "/" + c2.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", c2.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado2 = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();
            ArrayList<Long> idsResultado2 = new ArrayList<>();
            for (Cliente c : resultado2.getClientesInteressados()) {
                idsResultado2.add(c.getId());
            }

            //Assert
            assertAll(
                    () -> assertFalse(resultado2.getDisponivel()),
                    () -> assertEquals(2, resultado2.getClientesInteressados().size()),
                    () -> assertTrue(idsResultado2.contains(c2.getId()))
            );
        }

        @Test
        @DisplayName("Quando um cliente inexistente demonstra interesse em um sabor")
        void testDemonstrarInteresseClienteInexistente() throws Exception {
            //Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + "999999" + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isNotFound()) // Codigo 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando demonstramos interesse em um sabor com código de acesso inválido")
        void testDemonstrarInteresseCodigoAcessoInvalido() throws Exception {
            //Arrange
            // nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "000000")
                            .param("idSabor", s1.getId().toString())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isUnauthorized()) // Codigo 401
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando demonstramos interesse em um sabor inexistente")
        void testDemonstrarInteresseSaborInexistente() throws Exception {
            //Arrange
            // nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/interesse-sabor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idSabor", "9999999")
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isNotFound()) // Codigo 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("sabor inexistente!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do histórico de pedidos do cliente")
    class ClienteVerificacaoHistoricoPedidos {
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

        ObjectMapper objectMapper = new ObjectMapper();

        Cliente cliente1;
        Cliente cliente2;
        Entregador entregador;
        Sabor sabor1;
        Sabor sabor2;
        Pizza pizzaM1;
        Pizza pizzaG1;
        Pizza pizzaM2;
        Pizza pizzaG2;
        Estabelecimento estabelecimento;
        Pedido pedido_cliente1_recebido;
        Pedido pedido_cliente1_preparando;
        Pedido pedido_cliente1_pronto;
        Pedido pedido_cliente1_emrota;
        Pedido pedido_cliente1_entregue;
        Pedido pedido_cliente2;
        PedidoRequestDTO pedidoRequestDTO;
        PedidoRequestDTO pedidoRequestDTO2;
        @Autowired
        ModelMapper modelMapper;


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

            cliente1 = clienteRepository.save(Cliente.builder()
                    .nome("cliente1")
                    .endereco("Campina Grande")
                    .codigoAcesso("123456")
                    .build());

            cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("cliente2")
                    .endereco("Campina Grande")
                    .codigoAcesso("123456")
                    .build());

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Joãozinho")
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

            List<Pizza> pizzas2 = List.of(pizzaM2, pizzaG2);

            pedido_cliente1_recebido = Pedido.builder()
                    .valorPedido(35.0)
                    .clienteId(cliente1.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .entregadorId(entregador.getId())
                    .enderecoEntrega("Rua Jonas, 99")
                    .pizzas(List.of(pizzaM1, pizzaG1))
                    .statusPedido(StatusPedido.PEDIDO_RECEBIDO)
                    .statusPagamento(true)
                    .dataCriacao(LocalDateTime.now())
                    .build();


            pedido_cliente1_preparando = Pedido.builder()
                    .valorPedido(35.0)
                    .clienteId(cliente1.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .entregadorId(entregador.getId())
                    .enderecoEntrega("Rua Jonas, 99")
                    .pizzas(List.of(pizzaM1, pizzaG1))
                    .statusPedido(StatusPedido.PEDIDO_EM_PREPARO)
                    .statusPagamento(true)
                    .dataCriacao(LocalDateTime.now())
                    .build();


            pedido_cliente1_pronto = Pedido.builder()
                    .valorPedido(35.0)
                    .clienteId(cliente1.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .entregadorId(entregador.getId())
                    .enderecoEntrega("Rua Jonas, 99")
                    .pizzas(List.of(pizzaM1, pizzaG1))
                    .statusPedido(StatusPedido.PEDIDO_PRONTO)
                    .statusPagamento(true)
                    .dataCriacao(LocalDateTime.now())
                    .build();


            pedido_cliente1_emrota = Pedido.builder()
                    .valorPedido(35.0)
                    .clienteId(cliente1.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .entregadorId(entregador.getId())
                    .enderecoEntrega("Rua Jonas, 99")
                    .pizzas(List.of(pizzaM1, pizzaG1))
                    .statusPedido(StatusPedido.PEDIDO_EM_ROTA)
                    .statusPagamento(true)
                    .dataCriacao(LocalDateTime.now())
                    .build();


            pedido_cliente1_entregue = Pedido.builder()
                    .valorPedido(35.0)
                    .clienteId(cliente1.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .entregadorId(entregador.getId())
                    .enderecoEntrega("Rua Jonas, 99")
                    .pizzas(List.of(pizzaM1, pizzaG1))
                    .statusPedido(StatusPedido.PEDIDO_ENTREGUE)
                    .statusPagamento(true)
                    .dataCriacao(LocalDateTime.now())
                    .build();


            pedido_cliente2 = Pedido.builder()
                    .valorPedido(100.0)
                    .clienteId(cliente2.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .entregadorId(entregador.getId())
                    .enderecoEntrega("Rua das Castanholas, 99")
                    .pizzas(pizzas2)
                    .statusPedido(StatusPedido.PEDIDO_RECEBIDO)
                    .statusPagamento(true)
                    .dataCriacao(LocalDateTime.now())
                    .build();

            pedidoRepository.save(pedido_cliente1_pronto);
            pedidoRepository.save(pedido_cliente1_preparando);
            pedidoRepository.save(pedido_cliente1_entregue);
            pedidoRepository.save(pedido_cliente2);
            pedidoRepository.save(pedido_cliente1_emrota);
            pedidoRepository.save(pedido_cliente1_recebido);

            List<PizzaRequestDTO> pizzasDTO = pedido_cliente1_recebido.getPizzas().stream()
                    .map(pizza -> PizzaRequestDTO.builder()
                            .tamanho(pizza.getTamanho())
                            .sabor1(pizza.getSabor1().getNome())
                            .sabor2(pizza.getSabor2() != null ? pizza.getSabor2().getNome() : null)
                            .build())
                    .toList();

            List<PizzaRequestDTO> pizzasDTO2 = pedido_cliente2.getPizzas().stream()
                    .map(pizza -> PizzaRequestDTO.builder()
                            .tamanho(pizza.getTamanho())
                            .sabor1(pizza.getSabor1().getNome())
                            .sabor2(pizza.getSabor2() != null ? pizza.getSabor2().getNome() : null)
                            .build())
                    .toList();

            pedidoRequestDTO = PedidoRequestDTO.builder()
                    .enderecoEntrega(pedido_cliente1_recebido.getEnderecoEntrega())
                    .pizzas(pizzasDTO)
                    .build();

            pedidoRequestDTO2 = PedidoRequestDTO.builder()
                    .enderecoEntrega(pedido_cliente2.getEnderecoEntrega())
                    .pizzas(pizzasDTO2)
                    .build();

        }

        @AfterEach
        void tearDown() {
            pedidoRepository.deleteAll();
            clienteRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
            entregadorRepository.deleteAll();
            pizzaRepository.deleteAll();
            saborRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos")
        @Transactional
        void testConsultarHistoricoSemFiltro() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente1.getId().toString() + "/meus-pedidos")
                            .param("codigoAcesso", cliente1.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            List<PedidoResponseDTO> resultadoEsperado = List.of(pedido_cliente1_recebido, pedido_cliente1_preparando, pedido_cliente1_pronto, pedido_cliente1_emrota, pedido_cliente1_entregue).stream()
                    .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                    .toList();

            assertAll(
                    () -> assertEquals(5, resultado.size()),
                    () -> assertEquals(resultadoEsperado, resultado)
            );

            // Act 2
            String responseJsonString2 = driver.perform(get(URI_CLIENTES + "/" + cliente2.getId().toString() + "/meus-pedidos")
                            .param("codigoAcesso", cliente2.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert

            List<PedidoResponseDTO> resultadoEsperado2 = List.of(pedido_cliente2).stream()
                    .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                    .toList();

            List<PedidoResponseDTO> resultado2 = objectMapper.readValue(responseJsonString2, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(1, resultado2.size()),
                    () -> assertEquals(resultadoEsperado2, resultado2)
            );
        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos passando um código de acesso invalido")
        @Transactional
        void testConsultarSemFiltroCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente1.getId().toString() + "/meus-pedidos")
                            .param("codigoAcesso", "999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos de um cliente inexistente")
        @Transactional
        void testConsultarHistoricoSemFiltroClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/99999999/meus-pedidos")
                            .param("codigoAcesso", cliente1.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos, mas não temos historico de pedidos")
        @Transactional
        void testConsultarHistoricoSemFiltroHistoricoVazio() throws Exception {
            // Arrange
            Cliente cliente3 = clienteRepository.save(Cliente.builder()
                    .nome("cliente3")
                    .endereco("Campina Grande")
                    .codigoAcesso("111111")
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente3.getId().toString() + "/meus-pedidos")
                            .param("codigoAcesso", cliente3.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<PedidoResponseDTO> resultado2 = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            //Assert
            assertAll(
                    () -> assertEquals(0, resultado2.size())
            );
        }

        @Test
        @DisplayName("Quando consultamos um pedido especifico valido no historico de pedidos")
        @Transactional
        void testConsultarHistoricoPedidoEspecifico() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente1.getId().toString() + "/meus-pedidos/" + pedido_cliente1_entregue.getId().toString())
                            .param("codigoAcesso", cliente1.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(new PedidoResponseDTO(pedido_cliente1_entregue), resultado)
            );
        }

        @Test
        @DisplayName("Quando consultamos um pedido que não é nosso no historico de pedidos")
        @Transactional
        void testConsultarHistoricoPedidoAlheio() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente1.getId().toString() + "/meus-pedidos/" + pedido_cliente2.getId().toString())
                            .param("codigoAcesso", cliente1.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("O id informado para o cliente eh invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos um pedido especifico no historico de pedidos passando um codigo de acesso invalido")
        @Transactional
        void testConsultarHistoricoPedidoEspecificoCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente1.getId().toString() + "/meus-pedidos/" + pedido_cliente1_entregue.getId().toString())
                            .param("codigoAcesso", "999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos um pedido inexistente no historico de pedidos")
        @Transactional
        void testConsultarHistoricoPedidoInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente1.getId().toString() + "/meus-pedidos/99999999")
                            .param("codigoAcesso", cliente1.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("pedido inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos um pedido no historico de pedidos de um cliente inexistente")
        @Transactional
        void testConsultarHistoricoPedidoEspecificoClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/9999999/meus-pedidos/" + pedido_cliente1_entregue.getId().toString())
                            .param("codigoAcesso", cliente1.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos um pedido no historico de pedidos, mas nao temos historico de pedidos")
        @Transactional
        void testConsultarPedidoEspecificoHistoricoVazio() throws Exception {
            // Arrange
            Cliente cliente3 = clienteRepository.save(Cliente.builder()
                    .nome("cliente3")
                    .endereco("Campina Grande")
                    .codigoAcesso("111111")
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente3.getId().toString() + "/meus-pedidos/" + pedido_cliente2.getId().toString())
                            .param("codigoAcesso", cliente3.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            //Assert
            assertAll(
                    () -> assertEquals("O id informado para o cliente eh invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos filtrando por tipo")
        @Transactional
        void testConsultarHistoricoFiltrandoPorTipo() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente1.getId().toString() + "/meus-pedidos/status/" + pedido_cliente1_recebido.getStatusPedido().toString())
                            .param("codigoAcesso", cliente1.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            List<PedidoResponseDTO> resultadoEsperado = List.of(pedido_cliente1_recebido).stream()
                    .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                    .toList();

            assertAll(
                    () -> assertEquals(1, resultado.size()),
                    () -> assertEquals(resultadoEsperado, resultado)
            );

        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos filtrando por tipo, mas nao temos pedidos do tipo")
        @Transactional
        void testConsultarHistoricoFiltrandoPorTipoSemPedidosDoTipo() throws Exception {
            // Arrange
            // nenhuma necessidade além de setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente2.getId().toString() + "/meus-pedidos/status/" + StatusPedido.PEDIDO_EM_ROTA)
                            .param("codigoAcesso", cliente2.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(0, resultado.size())
            );
        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos filtrando por tipo, mas nao temos historico de pedidos")
        @Transactional
        void testConsultarHistoricoFiltrandoPorTipoHistoricoVazio() throws Exception {
            // Arrange
            Cliente cliente3 = clienteRepository.save(Cliente.builder()
                    .nome("cliente3")
                    .endereco("Campina Grande")
                    .codigoAcesso("111111")
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente3.getId().toString() + "/meus-pedidos/status/" + StatusPedido.PEDIDO_EM_ROTA)
                            .param("codigoAcesso", cliente3.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(0, resultado.size())
            );
        }


        @Test
        @DisplayName("Quando consultamos o historico de pedidos filtrando por tipo invalido")
        @Transactional
        void testConsultarHistoricoFiltrandoPorTipoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade de além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente2.getId().toString() + "/meus-pedidos/status/INVALIDO")
                            .param("codigoAcesso", cliente2.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertAll(
                    () -> assertEquals("Valor invalido para enum de statusPedido", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos filtrando por tipo passando codigo de acesso invalido")
        @Transactional
        void testConsultarHistoricoFiltrandoPorTipoCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade de além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente2.getId().toString() + "/meus-pedidos/status/" + pedido_cliente1_recebido.getStatusPedido().toString())
                            .param("codigoAcesso", "999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando consultamos o historico de pedidos filtrando por tipo de um cliente inexistente")
        @Transactional
        void testConsultarHistoricoFiltrandoPorTipoClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade de além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/999999/meus-pedidos/status/" + pedido_cliente1_recebido.getStatusPedido().toString())
                            .param("codigoAcesso", "999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de cancelamento dos pedidos pelo cliente")
    class ClienteVerificacaoCancelamentoPedidos {

        @Autowired
        PedidoRepository pedidoRepository;

        @Autowired
        EstabelecimentoRepository estabelecimentoRepository;

        @Autowired
        EntregadorRepository entregadorRepository;

        @Autowired
        PizzaRepository pizzaRepository;

        @Autowired
        SaborRepository saborRepository;

        Entregador entregador;
        Sabor sabor1;
        Sabor sabor2;
        Pizza pizzaM1;
        Pizza pizzaG1;
        Estabelecimento estabelecimento;
        Pedido pedido_cliente;

        @BeforeEach
        void setUp() {

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

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Joãozinho")
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

            pedido_cliente = Pedido.builder()
                    .valorPedido(35.0)
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .entregadorId(entregador.getId())
                    .enderecoEntrega("Rua Jonas, 99")
                    .pizzas(List.of(pizzaM1, pizzaG1))
                    .statusPedido(StatusPedido.PEDIDO_RECEBIDO)
                    .statusPagamento(true)
                    .dataCriacao(LocalDateTime.now())
                    .build();

            pedidoRepository.save(pedido_cliente);

        }

        @AfterEach
        void tearDown() {
            pedidoRepository.deleteAll();
            clienteRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
            entregadorRepository.deleteAll();
            pizzaRepository.deleteAll();
            saborRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando tentamos cancelar um pedido de um cliente inexistente")
        void testCancelarPedidoClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/999/cancelar/" + pedido_cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("cliente inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos cancelar um pedido de um cliente com codigo de acesso invalido")
        void testCancelarPedidoCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId().toString() + "/cancelar/" + pedido_cliente.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "000000"))
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
        @DisplayName("Quando tentamos cancelar um pedido inexistente de um cliente")
        void testCancelarPedidoInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId().toString() + "/cancelar/" + 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
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
        @DisplayName("Quando tentamos cancelar um pedido de outro cliente")
        void testCancelarPedidoClienteInvalido() throws Exception {
            // Arrange
            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("cliente2")
                    .endereco("Campina Grande")
                    .codigoAcesso("666666")
                    .build());

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente2.getId().toString() + "/cancelar/" + pedido_cliente.getId().toString())
                            .param("codigoAcesso", cliente2.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O id informado para o cliente eh invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando cancelamos um pedido com status recebido")
        void testCancelarPedidoStatusRecebido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId().toString() + "/cancelar/" + pedido_cliente.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertAll(
                    () -> assertTrue(responseJsonString.isBlank()),
                    () -> assertFalse(pedidoRepository.findById(pedido_cliente.getId()).isPresent())
            );
        }

        @Test
        @DisplayName("Quando cancelamos um pedido com status em preparo")
        void testCancelarPedidoStatusEmPreparo() throws Exception {
            // Arrange
            pedido_cliente.setStatusPedido(StatusPedido.PEDIDO_EM_PREPARO);
            pedidoRepository.save(pedido_cliente);

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId().toString() + "/cancelar/" + pedido_cliente.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertAll(
                    () -> assertTrue(responseJsonString.isBlank()),
                    () -> assertFalse(pedidoRepository.findById(pedido_cliente.getId()).isPresent())
            );
        }

        @Test
        @DisplayName("Quando tentamos cancelar um pedido com status pronto")
        void testCancelarPedidoStatusPronto() throws Exception {
            // Arrange
            pedido_cliente.setStatusPedido(StatusPedido.PEDIDO_PRONTO);
            pedidoRepository.save(pedido_cliente);

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId().toString() + "/cancelar/" + pedido_cliente.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O pedido nao pode mais ser cancelado", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos cancelar um pedido com status em rota")
        void testCancelarPedidoStatusEmRota() throws Exception {
            // Arrange
            pedido_cliente.setStatusPedido(StatusPedido.PEDIDO_EM_ROTA);
            pedidoRepository.save(pedido_cliente);

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId().toString() + "/cancelar/" + pedido_cliente.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O pedido nao pode mais ser cancelado", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos cancelar um pedido com status entregue")
        void testCancelarPedidoStatusEntregue() throws Exception {
            // Arrange
            pedido_cliente.setStatusPedido(StatusPedido.PEDIDO_ENTREGUE);
            pedidoRepository.save(pedido_cliente);

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId().toString() + "/cancelar/" + pedido_cliente.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O pedido nao pode mais ser cancelado", resultado.getMessage())
            );
        }
    }

}
