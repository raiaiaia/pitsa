package com.ufcg.psoft.pitsa.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.pitsa.dto.estabelecimento.EstabelecimentoRequestDTO;
import com.ufcg.psoft.pitsa.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborCardapioDTO;
import com.ufcg.psoft.pitsa.exception.CustomErrorType;
import com.ufcg.psoft.pitsa.model.Estabelecimento;
import com.ufcg.psoft.pitsa.model.Sabor;
import com.ufcg.psoft.pitsa.model.enums.TipoSabor;
import com.ufcg.psoft.pitsa.repository.EstabelecimentoRepository;
import com.ufcg.psoft.pitsa.repository.SaborRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Estabelecimento")
public class EstabelecimentoControllerTests {

    final String URI_ESTABELECIMENTO = "/estabelecimento";

    @Autowired
    MockMvc driver;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborRepository saborRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Estabelecimento estabelecimento;
    EstabelecimentoRequestDTO estabelecimentoRequestDTO;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .sabores(new ArrayList<>())
                .codigoAcesso("123456")
                .build()
        );
        estabelecimentoRequestDTO = EstabelecimentoRequestDTO.builder()
                .codigoAcesso(estabelecimento.getCodigoAcesso())
                .build();
    }

    @AfterEach
    void tearDown() {
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do código de acesso")
    class EstabelecimentoVerificacaoCodigoAcesso {

        @Test
        @DisplayName("Quando tentamos atualizar estabelecimento com código de acesso nulo")
        void testAtualizarEstabelecimentoCodigoAcessoNulo() throws Exception {
            // Arrange
            estabelecimentoRequestDTO.setCodigoAcesso(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTO + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
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
        @DisplayName("Quando tentamos atualizar estabelecimento com código de acesso tendo mais de 6 digitos")
        void testAtualizarEstabelecimentoCodigoAcessoMaisDe6Digitos() throws Exception {
            // Arrange
            estabelecimentoRequestDTO.setCodigoAcesso("1234567");

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTO + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
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
        @DisplayName("Quando tentamos atualizar estabelecimento com código de acesso tendo menos de 6 digitos")
        void testAtualizarEstabelecimentoCodigoAcessoMenosDe6Digitos() throws Exception {
            // Arrange
            estabelecimentoRequestDTO.setCodigoAcesso("12345");

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTO + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
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
        @DisplayName("Quando tentamos atualizar estabelecimento com código de acesso não numérico")
        void testAtualizarEstabelecimentoCodigoAcessoNaoNumerico() throws Exception {
            // Arrange
            estabelecimentoRequestDTO.setCodigoAcesso("a*c4e@");

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTO + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
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
    class EstabelecimentoVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando criamos um estabelecimento com dados válidos")
        void testCriarEstabelecimentoValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(post(URI_ESTABELECIMENTO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJsonString, EstabelecimentoResponseDTO.EstabelecimentoResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId())
            );

        }

        @Test
        @DisplayName("Quando atualizamos o estabelecimento com dados válidos")
        void testAtualizarEstabelecimentoValido() throws Exception {
            // Arrange
            Long estabelecimentoId = estabelecimento.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTO + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJsonString, EstabelecimentoResponseDTO.EstabelecimentoResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getId().longValue(), estabelecimentoId)
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar estabelecimento inexistente")
        void testAtualizarEstabelecimentoInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTO + "/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar estabelecimento passando código de acesso inválido")
        void testAtualizarEstabelecimentoCodigoAcessoInvalido() throws Exception {
            // Arrange
            Long estabelecimentoId = estabelecimento.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTO + "/" + estabelecimentoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "invalido")
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
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
        @DisplayName("Quando removemos um estabelecimento valido")
        void testRemoverEstabelecimentoValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ESTABELECIMENTO + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando tentamos remover um estabelecimento inexistente")
        void testRemoverEstabelecimentoInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ESTABELECIMENTO + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos remover um estabelecimento passando código de acesso inválido")
        void testRemoverEstabelecimentoCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ESTABELECIMENTO + "/" + estabelecimento.getId())
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
    @DisplayName("Conjunto de casos de verificação do Cardápio")
    class EstabelecimentoVerificacaoCardapio {

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um cardapio")
        void testRecuperarCardapioValido() throws Exception {
            // Arrange
            Sabor s1 = Sabor.builder()
                    .nome("Portuguesa")
                    .precoMedia(20.0)
                    .precoGrande(30.0)
                    .tipo(TipoSabor.SALGADO)
                    .build();
            Sabor s2 = Sabor.builder()
                    .nome("Marguerita")
                    .precoMedia(22.0)
                    .precoGrande(32.0)
                    .tipo(TipoSabor.SALGADO)
                    .build();
            Sabor s3 = Sabor.builder()
                    .nome("Banana Nevada")
                    .precoMedia(40.0)
                    .precoGrande(50.0)
                    .tipo(TipoSabor.DOCE)
                    .disponivel(false)
                    .build();
            Sabor s4 = Sabor.builder()
                    .nome("Doce de Leite")
                    .precoMedia(20.0)
                    .precoGrande(30.0)
                    .tipo(TipoSabor.DOCE)
                    .build();

            List<Sabor> sabores = new ArrayList<>(List.of(s1, s2, s3, s4));

            saborRepository.saveAll(sabores);
            estabelecimento.getSabores().addAll(sabores);
            estabelecimentoRepository.save(estabelecimento);

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento.getId() + "/cardapio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            List<SaborCardapioDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(4, resultado.size()),
                    () -> assertEquals(
                            sabores.stream()
                                    .sorted(Comparator.comparing(Sabor::getDisponivel, Comparator.reverseOrder()))
                                    .map(SaborCardapioDTO::new)
                                    .toList(), resultado)
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um cardapio que não tem sabores")
        void testRecuperarCardapioSemSabores() throws Exception {
            // Arrange
            // Criando um outro estabelecimento que tem sabores
            Sabor s1 = Sabor.builder()
                    .nome("Portuguesa")
                    .precoMedia(20.0)
                    .precoGrande(30.0)
                    .tipo(TipoSabor.SALGADO)
                    .build();

            Sabor s2 = Sabor.builder()
                    .nome("Doce de Leite")
                    .precoMedia(20.0)
                    .precoGrande(30.0)
                    .tipo(TipoSabor.DOCE)
                    .build();

            estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .sabores(List.of(s1, s2))
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento.getId() + "/cardapio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            List<SaborCardapioDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(0, resultado.size())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um cardapio do estabelecimento inexistente")
        void testRecuperarEstabelecimentoInexistenteCardapio() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + 9999999 + "/cardapio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um cardapio por tipo de sabor")
        void testRecuperarCardapioTipoValido() throws Exception {
            // Arrange
            Sabor s1 = saborRepository.save(Sabor.builder()
                    .nome("Portuguesa")
                    .precoMedia(20.0)
                    .precoGrande(30.0)
                    .tipo(TipoSabor.SALGADO)
                    .build());

            Sabor s2 = saborRepository.save(Sabor.builder()
                    .nome("Marguerita")
                    .precoMedia(22.0)
                    .precoGrande(32.0)
                    .tipo(TipoSabor.SALGADO)
                    .build());
            saborRepository.save(Sabor.builder()
                    .nome("Banana Nevada")
                    .precoMedia(40.0)
                    .precoGrande(50.0)
                    .tipo(TipoSabor.DOCE)
                    .disponivel(false)
                    .build());

            Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .sabores(List.of(s1, s2))
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento1.getId() + "/cardapio/" + TipoSabor.SALGADO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            List<SaborCardapioDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });
            List<Sabor> expected = List.of(s1, s2);

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(
                            expected.stream()
                                    .map(SaborCardapioDTO::new)
                                    .toList(), resultado)
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um cardapio por tipo de sabor sem sabor do tipo")
        void testRecuperarCardapioSemSaborDoTipo() throws Exception {
            // Arrange
            Sabor s1 = Sabor.builder()
                    .nome("Portuguesa")
                    .precoMedia(20.0)
                    .precoGrande(30.0)
                    .tipo(TipoSabor.SALGADO)
                    .build();

            Sabor s2 = Sabor.builder()
                    .nome("Marguerita")
                    .precoMedia(22.0)
                    .precoGrande(32.0)
                    .tipo(TipoSabor.SALGADO)
                    .build();

            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .sabores(List.of(s1, s2))
                    .build();

            estabelecimentoRepository.save(estabelecimento1);

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento1.getId() + "/cardapio/" + TipoSabor.DOCE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            List<SaborCardapioDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(0, resultado.size())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um cardapio por tipo de sabor sem nenhum sabor")
        void testRecuperarCardapioSemSaborAlgum() throws Exception {
            // Arrange
            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();

            estabelecimentoRepository.save(estabelecimento1);

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento1.getId() + "/cardapio/" + TipoSabor.DOCE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            List<SaborCardapioDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(0, resultado.size())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um cardapio por tipo de sabor invalido")
        void testRecuperarCardapioTipoInvalido() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + 9999999 + "/cardapio/" + "invalido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Valor invalido para enum de tipoSabor", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um cardapio por tipo do estabelecimento inexistente")
        void testRecuperarCardapioPorTipoEstabelecimentoInexistente() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + 9999999 + "/cardapio/" + TipoSabor.SALGADO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }
    }
}
