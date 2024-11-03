package com.ufcg.psoft.pitsa.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.pitsa.dto.entregador.EntregadorRequestDTO;
import com.ufcg.psoft.pitsa.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.pitsa.dto.veiculo.VeiculoRequestDTO;
import com.ufcg.psoft.pitsa.exception.CustomErrorType;
import com.ufcg.psoft.pitsa.model.Entregador;
import com.ufcg.psoft.pitsa.model.Veiculo;
import com.ufcg.psoft.pitsa.model.enums.TipoVeiculo;
import com.ufcg.psoft.pitsa.repository.EntregadorRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Entregadores")
public class EntregadorControllerTests {

    final String URI_ENTREGADOR = "/entregador";

    @Autowired
    MockMvc driver;

    @Autowired
    EntregadorRepository entregadorRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Entregador entregador;
    EntregadorRequestDTO entregadorRequestDTO;
    Veiculo veiculo;
    VeiculoRequestDTO veiculoRequestDTO;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        veiculo = new Veiculo("SLD-1B54", TipoVeiculo.CARRO, "Azul");
        veiculoRequestDTO = VeiculoRequestDTO.builder()
                .placaVeiculo(veiculo.getPlacaVeiculo())
                .tipoVeiculo(veiculo.getTipoVeiculo())
                .corVeiculo(veiculo.getCorVeiculo())
                .build();
        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Entregador Um da Silva")
                .veiculo(veiculo)
                .codigoAcesso("123456")
                .build()
        );
        entregadorRequestDTO = EntregadorRequestDTO.builder()
                .nome(entregador.getNome())
                .veiculoRequestDTO(veiculoRequestDTO)
                .codigoAcesso(entregador.getCodigoAcesso())
                .build();
    }

    @AfterEach
    void tearDown() {
        entregadorRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class EntregadorVerificacaoNome {

        @Test
        @DisplayName("Quando atualizamos entregador com nome válido")
        void testAtualizarEntregadorNomeValido() throws Exception {
            // Arrange
            entregadorRequestDTO.setNome("Entregador Um Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.EntregadorResponseDTOBuilder.class).build();

            // Assert
            assertEquals("Entregador Um Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando tentamos atualizar entregador com nome nulo")
        void testAtualizarEntregadorNomeNulo() throws Exception {
            // Arrange
            entregadorRequestDTO.setNome(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
        @DisplayName("Quando tentamos atualizar entregador com nome vazio")
        void testAtualizarEntregadorNomeVazio() throws Exception {
            // Arrange
            entregadorRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
    @DisplayName("Conjunto de casos de verificação da placa do veiculo")
    class EntregadorVerificacaoPlacaVeiculo {

        @Test
        @DisplayName("Quando atualizamos entregador com placa de veículo válida")
        void testAtualizarEntregadorPlacaVeiculoValida() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setPlacaVeiculo("FIO-1G44");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.EntregadorResponseDTOBuilder.class).build();

            // Assert
            assertEquals("FIO-1G44", resultado.getVeiculoResponseDTO().getPlacaVeiculo());
        }

        @Test
        @DisplayName("Quando tentamos atualizar entregador com placa de veículo nula")
        void testAtualizarEntregadorPlacaVeiculoNula() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setPlacaVeiculo(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Placa do veiculo obrigatoria", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar entregador com placa de veículo no formato invalido")
        void testAtualizarEntregadorPlacaVeiculoFormatoInvalido() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setPlacaVeiculo("SLD1B54");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Placa do veiculo deve ser no formato AAA-1234 ou AAA-1A23", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar entregador com placa de veículo vazia")
        void testAtualizarEntregadorPlacaVeiculoVazia() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setPlacaVeiculo("");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue(resultado.getErrors().contains("Placa do veiculo obrigatoria"))

            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do tipo do veiculo")
    class EntregadorVerificacaoTipoVeiculo {

        @Test
        @DisplayName("Quando atualizamos entregador com tipo de veículo válido")
        void testAtualizarEntregadorTipoVeiculoValido() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setTipoVeiculo(TipoVeiculo.CARRO);

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.EntregadorResponseDTOBuilder.class).build();

            // Assert
            assertEquals(TipoVeiculo.CARRO, resultado.getVeiculoResponseDTO().getTipoVeiculo());
        }

        @Test
        @DisplayName("Quando tentamos atualizar entregador com tipo de veículo nulo")
        void testAtualizarEntregadorTipoVeiculoNulo() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setTipoVeiculo(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Tipo do veiculo obrigatorio", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da cor do veiculo")
    class EntregadorVerificacaoCorDoVeiculo {

        @Test
        @DisplayName("Quando atualizamos entregador com cor de veículo válida")
        void testAtualizarEntregadorCorVeiculoValida() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setCorVeiculo("Amarelo");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.EntregadorResponseDTOBuilder.class).build();

            // Assert
            assertEquals("Amarelo", resultado.getVeiculoResponseDTO().getCorVeiculo());
        }

        @Test
        @DisplayName("Quando tentamos atualizar entregador com cor de veículo nula")
        void testAtualizarEntregadorCorVeiculoNula() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setCorVeiculo(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Cor do veiculo obrigatoria", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar entregador com cor de veículo vazia")
        void testAtualizarEntregadorCorVeiculoVazio() throws Exception {
            // Arrange
            entregadorRequestDTO.getVeiculoRequestDTO().setCorVeiculo("");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Cor do veiculo obrigatoria", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do código de acesso")
    class EntregadorVerificacaoCodigoAcesso {

        @Test
        @DisplayName("Quando tentamos atualizar entregador com código de acesso nulo")
        void testAtualizarEntregadorCodigoAcessoNulo() throws Exception {
            // Arrange
            entregadorRequestDTO.setCodigoAcesso(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
        @DisplayName("Quando tentamos atualizar entregador com código de acesso tendo mais de 6 digitos")
        void testAtualizarEntregadorCodigoAcessoMaisDe6Digitos() throws Exception {
            // Arrange
            entregadorRequestDTO.setCodigoAcesso("1234567");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
        @DisplayName("Quando tentamos atualizar entregador com código de acesso tendo menos de 6 digitos")
        void testAtualizarEntregadorCodigoAcessoMenosDe6Digitos() throws Exception {
            // Arrange
            entregadorRequestDTO.setCodigoAcesso("12345");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
        @DisplayName("Quando tentamos atualizar entregador com código de acesso não numérico")
        void testAtualizarEntregadorCodigoAcessoNaoNumerico() throws Exception {
            // Arrange
            entregadorRequestDTO.setCodigoAcesso("a*c4e@");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
    class EntregadorVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando listamos todos os entregadores salvos")
        void testListarEntregadoresSalvos() throws Exception {
            // Arrange
            // Vamos ter 3 entregadores no banco
            Entregador entregador1 = Entregador.builder()
                    .nome("Entregador Dois Almeida")
                    .veiculo(new Veiculo("SLD-1B54", TipoVeiculo.MOTO, "Vermelho"))
                    .codigoAcesso("246810")
                    .build();
            Entregador entregador2 = Entregador.builder()
                    .nome("Entregador Tres Souto")
                    .veiculo(new Veiculo("SLD-1B78", TipoVeiculo.CARRO, "Branco"))
                    .codigoAcesso("246811")
                    .build();
            entregadorRepository.saveAll(Arrays.asList(entregador1, entregador2));

            // Act
            String responseJsonString = driver.perform(get(URI_ENTREGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Entregador> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(3, resultado.size())
            );
        }

        @Test
        @DisplayName("Quando recuperamos um entregador salvo pelo id")
        void testRecuperarEntregadorSalvo() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(entregador.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(entregador.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando recuperamos um entregador inexistente")
        void testRecuperarEntregadorInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_ENTREGADOR + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("entregador inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um entregador com dados válidos")
        void testCriarEntregadorValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(post(URI_ENTREGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Entregador resultado = objectMapper.readValue(responseJsonString, Entregador.EntregadorBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(entregadorRequestDTO.getNome(), resultado.getNome())
            );

        }

        @Test
        @DisplayName("Quando criamos um entregador com veiculo nulo")
        void testCriarEntregadorVeiculoNulo() throws Exception {
            // Arrange
            entregadorRequestDTO.setVeiculoRequestDTO(null);

            // Act
            String responseJsonString = driver.perform(post(URI_ENTREGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Veiculo obrigatorio", resultado.getErrors().get(0))
            );

        }

        @Test
        @DisplayName("Quando atualizamos o entregador com dados válidos")
        void testAtualizarEntregadorValido() throws Exception {
            // Arrange
            Long entregadorId = entregador.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Entregador resultado = objectMapper.readValue(responseJsonString, Entregador.EntregadorBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getId().longValue(), entregadorId),
                    () -> assertEquals(entregadorRequestDTO.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando atualizamos um entregador com veiculo nulo")
        void testAtualizarEntregadorVeiculoNulo() throws Exception {
            // Arrange
            entregadorRequestDTO.setVeiculoRequestDTO(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Veiculo obrigatorio", resultado.getErrors().get(0))
            );

        }

        @Test
        @DisplayName("Quando atualizamos o entregador inexistente")
        void testAtualizarEntregadorInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act & Assert
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO))
                            .param("codigoAcesso", entregador.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("entregador inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando atualizamos o entregador passando código de acesso inválido")
        void testAtualizarEntregadorCodigoAcessoInvalido() throws Exception {
            // Arrange
            Long entregadorId = entregador.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADOR + "/" + entregadorId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "invalido")
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
        @DisplayName("Quando removemos um entregador salvo")
        void testRemoverEntregadorValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ENTREGADOR + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando tentamos remover um entregador inexistente")
        void testRemoverEntregadorInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act & Assert

            String responseJsonString = driver.perform(delete(URI_ENTREGADOR + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("entregador inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos remover um entregador passando código de acesso inválido")
        void testRemoverEntregadorCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ENTREGADOR + "/" + entregador.getId())
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
}
