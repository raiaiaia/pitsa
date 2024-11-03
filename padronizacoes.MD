### Nomenclaturas
- Padronizar nomes de metodos dos services/controllers
  - create: criar
  - get: recuperar
  - getAll: listar
  - update: atualizar
  - delete: remover


- Nomenclatura de arquivos:
    - entidadeService, entidadeController, entidadeRepository
    - DTOs: entidadeRequestDTO e entidadeResponseDTO


- Nomenclatura de endpoints das entidades: todas no singular
```
@RequestMapping(
        value = "/cliente",
        produces = MediaType.APPLICATION_JSON_VALUE
)
```

- Nomenclatura de testes
    - nome de classe: Nome da Entidade + "Verificacao" + Caso de Verificação
    - descrição de teste: Quando -ação- -objeto do teste- -caso de verificação-
    - nome do teste: "test" + Verbo da Ação + Nome da Entidade/Objeto + Caso de Verificação
```
  @Nested
  @DisplayName("Conjunto de casos de verificação de criacao de associacao")
  class AssociacaoVerificacaoCriacao {
        @Test
        @DisplayName("Quando criamos uma associacao com sucesso")
        void testCriarAssociacaoComSucesso() throws Exception {
```
---
### Validações e Mensagens de erro
- Mensagens de erro: todas herdam de PitsAException e todas tem status http mapeado no `ErrorHandlingControllerAdvice`
  - `EntidadeNaoExisteException(entidadeNome)` "{entidadeNome} inexistente!"
  - `RelacionamentoNaoExisteException(message)`
  - `CodigoDeAcessoInvalidoException()` "Codigo de acesso invalido!"
  - `OperacaoInvalidaException(operandoNome)` "O {operandoNome} nao pode ser alterado!"
  - `TipoInvalidoException(entidadeNome)` "Valor invalido para enum de {entidadeNome}"


- Fazer criação de validator para casos de validações repetidas em diferentes métodos
- Verificação de código de acesso -> CodigoAcessoValidator
- Para verificação de atributos string nos DTOs, preferir `@NotBlank`ao invés de `@NotNull` para os atributos string
---
### Requisições e camada de serviço

- Colocar regex para validação de id nas requisições, ex: `@GetMapping("/{id}:[0-9]+")`
- Services buscam fkeys por objeto por ID
- Uso de generics nos services: `Service<I,O>` :: RequestDTO e ResponseDTO