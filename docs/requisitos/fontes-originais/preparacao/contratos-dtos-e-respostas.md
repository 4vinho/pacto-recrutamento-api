# Contratos, DTOs e respostas

## Resposta padrão

Todas as respostas da aplicação expostas pela API devem utilizar uma estrutura comum:

```json
{
  "statusCode": 201,
  "message": "Candidatura criada com sucesso.",
  "data": {
    "id": "01900000-0000-7000-8000-000000000001"
  }
}
```

Modelo conceitual:

```text
ApiResponse<T>
├── statusCode: int
├── message: String
└── data: T
```

O nome do campo deve ser `data`, corrigindo a grafia inicial `tdata`. O `T` representa o tipo genérico na classe Java, não faz parte do nome JSON.

## Respostas sem dados

Quando não houver conteúdo de retorno:

```json
{
  "statusCode": 200,
  "message": "Operação realizada com sucesso.",
  "data": null
}
```

## Resposta de validação

```json
{
  "statusCode": 400,
  "message": "A requisição possui campos inválidos.",
  "data": {
    "errors": [
      {
        "field": "email",
        "message": "deve ser um endereço de e-mail válido"
      }
    ]
  }
}
```

## Regras

- `statusCode` deve ser igual ao status HTTP.
- `message` deve ser segura para apresentação ao cliente.
- `data` deve conter um DTO, nunca uma entidade JPA.
- Exceções internas e stack traces não devem ser expostos.
- Mensagens técnicas detalhadas devem ficar somente nos logs.
- Datas devem ser serializadas em ISO 8601 e UTC.
- Campos sensíveis nunca devem aparecer nas respostas.

## DTOs

Cada endpoint deve possuir DTOs específicos.

Exemplo:

```text
CadastrarUsuarioRequest
LoginRequest
TokenResponse
CriarCandidatoRequest
CriarVagaRequest
CriarCandidaturaRequest
ResponderCandidaturaRequest
RespostaRequest
```

Não utilizar um DTO genérico de criação e atualização quando as regras forem diferentes.

## Respostas em lote

O endpoint:

```http
POST /candidaturas/{candidaturaId}/respostas
```

deve aceitar uma ou mais respostas:

```json
{
  "respostas": [
    {
      "perguntaId": "01900000-0000-7000-8000-000000000001",
      "valor": "Resposta"
    }
  ]
}
```

Validações:

- `respostas` é obrigatório.
- Deve conter pelo menos um item.
- Não pode repetir `perguntaId`.
- Cada pergunta deve pertencer à vaga da candidatura.
- Cada valor deve respeitar o tipo da pergunta.
- O lote inteiro deve ser confirmado ou rejeitado.

## Tratamento global de erros

Planejar um componente em `web/shared` para converter exceções em respostas:

- Validação: HTTP 400.
- Credenciais inválidas: HTTP 401.
- Sem permissão: HTTP 403.
- Recurso inexistente: HTTP 404.
- Conflito de negócio: HTTP 409.
- Erro inesperado: HTTP 500.

## Observação sobre duplicidade

O campo `statusCode` repete uma informação que já existe no protocolo HTTP. Ele será mantido porque foi definido como requisito do contrato da aplicação.
