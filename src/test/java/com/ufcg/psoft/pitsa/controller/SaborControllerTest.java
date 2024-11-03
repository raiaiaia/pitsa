package com.ufcg.psoft.pitsa.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.pitsa.dto.sabor.SaborRequestDTO;
import com.ufcg.psoft.pitsa.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.pitsa.exception.CustomErrorType;
import com.ufcg.psoft.pitsa.model.Cliente;
import com.ufcg.psoft.pitsa.model.Estabelecimento;
import com.ufcg.psoft.pitsa.model.Sabor;
import com.ufcg.psoft.pitsa.model.enums.TipoSabor;
import com.ufcg.psoft.pitsa.repository.ClienteRepository;
import com.ufcg.psoft.pitsa.repository.EstabelecimentoRepository;
import com.ufcg.psoft.pitsa.repository.SaborRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Sabores")
class SaborControllerTest {

    final String URI_SABOR = "/sabor";

    @Autowired
    MockMvc driver;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Sabor saborSalgado;
    SaborRequestDTO saborRequestDTO;
    Estabelecimento estabelecimento;

    @BeforeEach
    void setUp() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        saborSalgado = saborRepository.save(Sabor.builder()
                .nome("Portuguesa")
                .tipo(TipoSabor.SALGADO)
                .precoGrande(20.)
                .precoMedia(10.)
                .build()
        );
        saborRequestDTO = SaborRequestDTO.builder()
                .nome("p2")
                .tipo(TipoSabor.DOCE)
                .precoGrande(30.)
                .precoMedia(15.)
                .disponivel(Boolean.TRUE)
                .build();

        ArrayList<Sabor> sabors = new ArrayList<>();
        sabors.add(saborSalgado);
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .sabores(sabors)
                .codigoAcesso("123456")
                .build());
    }

    @AfterEach
    void tearDown() {
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do nome")
    class SaborVerificacaoNome {

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando nome em branco")
        void testAtualizarSaborNomeBlank() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("")
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome do sabor obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando nome null")
        void testAtualizarSaborNomeNull() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome(null)
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome do sabor obrigatorio", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos preços")
    class SaborVerificacaoPreco {

        @Test
        @Transactional
        @DisplayName("Quando tentamos criar um sabor com precoGrande negativo")
        void testCriarSaborPrecoGNegativo() throws Exception {
            // Arrange
            saborRequestDTO.setPrecoGrande(-1.);

            //Act
            String responseJsonString = driver.perform(post(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Preco deve ser maior que zero", resultado.getErrors().get(0))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos criar um sabor com precoGrande zero")
        void testCriarSaborPrecoGZero() throws Exception {
            // Arrange
            saborRequestDTO.setPrecoGrande(0.);

            //Act
            String responseJsonString = driver.perform(post(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Preco deve ser maior que zero", resultado.getErrors().get(0))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos criar um sabor com precoMedia negativo")
        void testCriarSaborPrecoMNegativo() throws Exception {
            // Arrange
            saborRequestDTO.setPrecoMedia(-1.);

            //Act
            String responseJsonString = driver.perform(post(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Preco deve ser maior que zero", resultado.getErrors().get(0))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos criar um sabor precoMedia igual a zero")
        void testCriarSaborPrecoMZero() throws Exception {
            // Arrange
            saborRequestDTO.setPrecoMedia(0.);

            //Act
            String responseJsonString = driver.perform(post(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Preco deve ser maior que zero", resultado.getErrors().get(0))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando precoGrande negativo")
        void testAtualizarSaborPrecoGNegativo() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("Alterado")
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(-10.)
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Preco deve ser maior que zero", resultado.getErrors().get(0))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando precoMedia negativo")
        void testAtualizarSaborPrecoMNegativo() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("Alterado")
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(-10.)
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Preco deve ser maior que zero", resultado.getErrors().get(0))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando preco null")
        void testAtualizarSaborPrecoNull() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome(saborSalgado.getNome())
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(null)
                    .precoMedia(null)
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> resultado.getErrors().contains("PrecoGrande obrigatorio"),
                    () -> resultado.getErrors().contains("PrecoMedia obrigatorio")
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do tipo")
    class SaborVerificacaoTipo {

        @Test
        @Transactional
        @DisplayName("Quando tentamos criar um sabor com tipo null")
        void testCriarSaborTipoNull() throws Exception {
            // Arrange
            saborRequestDTO.setTipo(null);

            //Act
            String responseJsonString = driver.perform(post(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Tipo obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando tipo null")
        void testAtualizarSaborTipoNull() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("Alterado")
                    .tipo(null)
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Tipo obrigatorio", resultado.getErrors().get(0))
            );
        }

    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de codigo de acesso do estabelecimento")
    class SaborVerificacaoCodigoAcessoEstabelecimento {

        @Test
        @Transactional
        @DisplayName("Quando tentamos criar um sabor passando estabelecimento existente e codigo de acesso invalido")
        void testCriarSaborEstabelecimentoExistenteCodigoInvalido() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(post(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", "invalido")
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );

        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos listar os sabores de um estabelecimento existente com código inválido")
        void testListarSaboresEstabelecimentoCodigoInvalido() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(get(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", "invalido")
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos remover um sabor passando um estabelecimento existente com codigo invalido")
        void testRemoverSaborEstabelecimentoExistenteCodigoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act & Assert
            String responseJsonString = driver.perform(delete(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", "000000"))
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
        @Transactional
        @DisplayName("Quando tentamos recuperar um sabor passando um estabelecimento com codigo de acesso invalido")
        void testRecuperarSaborEstabelecimentoCodigoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_SABOR + "/" + saborSalgado.getId())
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", "000000")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
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
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando um estabelecimento com codigo de acesso invalido")
        void testAtualizarSaborEstabelecimentoCodigoInvalido() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("Alterado")
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", "000000")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
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
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class SaborVerificacaoFluxosBasicosApiRest {

        @Test
        @Transactional
        @DisplayName("Quando listamos os sabores de um estabelecimento passando dados válidos")
        void testListarSaboresValido() throws Exception {
            // Arrange
            // criando mais 2 sabores alem do existente no setup, um deles esta em outro estabelecimento (nao entra na listagem)
            Sabor s1 = saborRepository.save(Sabor.builder()
                    .nome("Bacon")
                    .tipo(TipoSabor.SALGADO)
                    .precoGrande(20.)
                    .precoMedia(10.)
                    .build()
            );

            saborRepository.save(Sabor.builder()
                    .nome("Chocolate")
                    .tipo(TipoSabor.DOCE)
                    .precoGrande(20.)
                    .precoMedia(10.)
                    .build()
            );

            estabelecimento.getSabores().add(s1);
            estabelecimentoRepository.save(estabelecimento);

            //Act
            String responseJsonString = driver.perform(get(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size())
            );

        }

        @Test
        @Transactional
        @DisplayName("Quando listamos os sabores de um estabelecimento quando ele não tem sabores")
        void testListarSaboresNaoTemSabores() throws Exception {
            // Arrange
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build());

            //Act
            String responseJsonString = driver.perform(get(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(0, resultado.size())
            );

        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos listar os sabores de um estabelecimento inexistente")
        void testListarSaboresEstabelecimentoInexistente() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(get(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", "000000")
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando criamos um sabor com dados validos")
        void testCriarSaborValido() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(post(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(saborRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(saborRequestDTO.getTipo(), resultado.getTipo()),
                    () -> assertEquals(saborRequestDTO.getPrecoMedia(), resultado.getPrecoMedia()),
                    () -> assertEquals(saborRequestDTO.getPrecoGrande(), resultado.getPrecoGrande()),
                    () -> assertEquals(saborRequestDTO.getDisponivel(), resultado.getDisponivel())
            );


        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos criar um sabor passando um estabelecimento inexistente")
        void testCriarSaborEstabelecimentoInexistente() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            //Act
            String responseJsonString = driver.perform(post(URI_SABOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", "00000")
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );

        }

        @Test
        @Transactional
        @DisplayName("Quando removemos um sabor passando dados validos")
        void testRemoverSaborValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos remover um sabor inexistente")
        void testRemoverSaborInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_SABOR + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", "999999")
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("sabor inexistente!", resultado.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos remover sabor passando um estabelecimento inexistente")
        void testRemoverSaborEstabelecimentoInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act & Assert
            String responseJsonString = driver.perform(delete(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", "000000")
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos remover um sabor passando um estabelecimento invalido, ao qual o sabor não pertence")
        void testRemoverSaborEstabelecimentoInvalido() throws Exception {
            // Arrange
            Estabelecimento e2 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("223456")
                    .build());

            // Act & Assert
            String responseJsonString = driver.perform(delete(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", e2.getId().toString())
                            .param("codAcessoEstabelecimento", e2.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("Sabor inexistente no estabelecimento consultado!", resultado.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("Quando recuperamos um sabor valido")
        void testRecuperarSaborValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_SABOR + "/" + saborSalgado.getId())
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(saborSalgado.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(saborSalgado.getNome(), resultado.getNome()),
                    () -> assertEquals(saborSalgado.getTipo(), resultado.getTipo()),
                    () -> assertEquals(saborSalgado.getDisponivel(), resultado.getDisponivel()),
                    () -> assertEquals(saborSalgado.getPrecoGrande(), resultado.getPrecoGrande()),
                    () -> assertEquals(saborSalgado.getPrecoMedia(), resultado.getPrecoMedia())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos recuperar um sabor inexistente")
        void testRecuperarSaborInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_SABOR + "/" + 999999)
                            .param("id", "999999")
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("sabor inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos recuperar um sabor passando um estabelecimento invalido, ao qual o sabor não pertence")
        void testRecuperarSaborEstabelecimentoInvalido() throws Exception {
            // Arrange
            Estabelecimento e2 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("223456")
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_SABOR + "/" + saborSalgado.getId())
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", e2.getId().toString())
                            .param("codAcessoEstabelecimento", e2.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("Sabor inexistente no estabelecimento consultado!", resultado.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos recuperar um sabor passando um estabelecimento inexistente")
        void testRecuperarSaborEstabelecimentoInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_SABOR + "/" + saborSalgado.getId())
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", "000000")
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando alteramos um sabor valido")
        void testAtualizarSaborValido() throws Exception {
            // Arrange
            Long id = saborSalgado.getId();
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("Alterado")
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", id.toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getId().longValue(), id),
                    () -> assertEquals(saborRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(saborRequestDTO.getTipo(), resultado.getTipo()),
                    () -> assertEquals(saborRequestDTO.getPrecoMedia(), resultado.getPrecoMedia()),
                    () -> assertEquals(saborRequestDTO.getPrecoGrande(), resultado.getPrecoGrande()),
                    () -> assertEquals(saborRequestDTO.getDisponivel(), resultado.getDisponivel())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor inexistente")
        void testAtualizarSaborInexistente() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("Alterado")
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/999999")
                            .param("id", "999999")
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("sabor inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando um estabelecimento inexistente")
        void testAtualizarSaborEstabelecimentoInexistente() throws Exception {
            // Arrange
            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("Alterado")
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", "000000")
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar um sabor passando um estabelecimento invalido, ao qual o sabor não pertence")
        void testAtualizarSaborEstabelecimentoInvalido() throws Exception {

            // Arrange
            //criando um novo estabelecimento
            Estabelecimento e2 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("223456")
                    .build());

            saborRequestDTO = SaborRequestDTO.builder()
                    .nome("Alterado")
                    .tipo(saborSalgado.getTipo())
                    .precoGrande(saborSalgado.getPrecoGrande())
                    .precoMedia(saborSalgado.getPrecoMedia())
                    .disponivel(Boolean.TRUE)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId())
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", e2.getId().toString())
                            .param("codAcessoEstabelecimento", e2.getCodigoAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("Sabor inexistente no estabelecimento consultado!", resultado.getMessage());
        }

    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da atualização de disponibilidade de um sabor")
    class SaborVerificacaoDisponibilidade {

        Sabor s1;
        Sabor s2;
        Estabelecimento estabelecimento1;
        Cliente cliente1;
        Cliente cliente2;
        @Autowired
        ClienteRepository clienteRepository;
        private ByteArrayOutputStream logOutputStream;
        private PrintStream originalOut;

        @BeforeEach
        void setup() {
            logOutputStream = new ByteArrayOutputStream();
            originalOut = System.out;
            System.setOut(new PrintStream(logOutputStream));

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

            estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .sabores(List.of(s1, s2))
                    .build());

            cliente1 = clienteRepository.save(Cliente.builder()
                    .codigoAcesso("180405")
                    .nome("fulano")
                    .endereco("rua tal")
                    .build());

            cliente2 = clienteRepository.save(Cliente.builder()
                    .codigoAcesso("050505")
                    .nome("fulano de tal")
                    .endereco("rua tal")
                    .build());

        }

        @AfterEach
        void tearDown() {
            System.setOut(originalOut);
            clienteRepository.deleteAll();
            saborRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar disponibilidade de um sabor inexistente")
        void testAtualizarDisponibilidadeSaborInexistente() throws Exception {
            // Arrange
            // nada alem do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/999999" + "/disponibilidade")
                            .param("id", "999999")
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento1.getCodigoAcesso())
                            .param("disponibilidade", String.valueOf(Boolean.TRUE))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("sabor inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar disponibilidade de um sabor passando um estabelecimento inexistente")
        void testAtualizarDisponibilidadeSaborEstabelecimentoInexistente() throws Exception {
            // Arrange
            // nada alem do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + saborSalgado.getId() + "/disponibilidade")
                            .param("id", saborSalgado.getId().toString())
                            .param("estabelecimentoId", "000000")
                            .param("codAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("disponibilidade", String.valueOf(Boolean.TRUE))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertAll(
                    () -> assertEquals("estabelecimento inexistente!", resultado.getMessage())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar disponibilidade de um sabor passando um codigo de acesso invalido")
        void testAtualizarDisponibilidadeSaborCodigoInvalido() throws Exception {
            // Arrange
            // nada alem do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + s1.getId() + "/disponibilidade")
                            .param("id", s1.getId().toString())
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("codAcessoEstabelecimento", "000000")
                            .param("disponibilidade", String.valueOf(Boolean.TRUE))
                            .contentType(MediaType.APPLICATION_JSON))
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
        @Transactional
        @DisplayName("Quando tentamos atualizar disponibilidade de um sabor passando um estabelecimento invalido, ao qual o sabor não pertence")
        void testAtualizarDisponibilidadeSaborEstabelecimentoInvalido() throws Exception {
            // Arrange
            Estabelecimento e2 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("223456")
                    .build());

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + s1.getId() + "/disponibilidade")
                            .param("id", s1.getId().toString())
                            .param("estabelecimentoId", e2.getId().toString())
                            .param("codAcessoEstabelecimento", e2.getCodigoAcesso())
                            .param("disponibilidade", String.valueOf(Boolean.TRUE))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(saborRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Assert
            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("Sabor inexistente no estabelecimento consultado!", resultado.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar disponibilidade de um sabor para false")
        void testAtualizarDisponibilidadeSaborFalse() throws Exception {
            // Arrange
            // nada alem do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + s2.getId() + "/disponibilidade")
                            .param("id", s2.getId().toString())
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento1.getCodigoAcesso())
                            .param("disponibilidade", String.valueOf(Boolean.FALSE))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            //Assert
            assertAll(
                    () -> assertEquals(s2.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(Boolean.FALSE, resultado.getDisponivel())
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar disponibilidade de um sabor para true sem clientes interessados")
        void testAtualizarDisponibilidadeSaborTrueSemInteresses() throws Exception {
            // Arrange
            // nada alem do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + s1.getId() + "/disponibilidade")
                            .param("id", s1.getId().toString())
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento1.getCodigoAcesso())
                            .param("disponibilidade", String.valueOf(Boolean.TRUE))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });
            String logContent = logOutputStream.toString();

            //Assert de corretude de valores
            assertAll(
                    () -> assertEquals(s1.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(Boolean.TRUE, resultado.getDisponivel())
            );

            //Assert de notificação de disponibilidade para os clientes interessados
            assertAll(
                    () -> assertFalse(logContent.contains("Cliente " + cliente1.getNome() + ", o sabor " + s1.getNome() + " de seu interesse está disponível!")),
                    () -> assertFalse(logContent.contains("Cliente " + cliente2.getNome() + ", o sabor " + s1.getNome() + " de seu interesse está disponível!"))
            );
        }

        @Test
        @Transactional
        @DisplayName("Quando tentamos atualizar disponibilidade de um sabor para true com clientes interessados")
        void testAtualizarDisponibilidadeSaborTrueComInteresses() throws Exception {
            // Arrange
            s1.addClienteInteressado(cliente1);
            s1.addClienteInteressado(cliente2);
            saborRepository.save(s1);

            // Act
            String responseJsonString = driver.perform(put(URI_SABOR + "/" + s1.getId() + "/disponibilidade")
                            .param("id", s1.getId().toString())
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("codAcessoEstabelecimento", estabelecimento1.getCodigoAcesso())
                            .param("disponibilidade", String.valueOf(Boolean.TRUE))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });
            String logContent = logOutputStream.toString();

            //Assert de corretude de valores
            assertAll(
                    () -> assertEquals(s1.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(Boolean.TRUE, resultado.getDisponivel()),
                    () -> assertEquals(0, resultado.getClientesInteressados().size())
            );
            //Assert de notificação de disponibilidade para os clientes interessados
            assertAll(
                    () -> assertTrue(logContent.contains("Cliente " + cliente1.getNome() + ", o sabor " + s1.getNome() + " de seu interesse está disponível!")),
                    () -> assertTrue(logContent.contains("Cliente " + cliente2.getNome() + ", o sabor " + s1.getNome() + " de seu interesse está disponível!"))
            );
        }
    }

}