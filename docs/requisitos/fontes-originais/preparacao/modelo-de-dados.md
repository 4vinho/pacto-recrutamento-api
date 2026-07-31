# Modelo de dados

## Convenções

- Tabelas no plural e em `snake_case`.
- Colunas em `snake_case`.
- Chaves primárias chamadas `id`.
- Chaves estrangeiras terminadas em `_id`.
- UUID como tipo dos identificadores.
- Datas com `timestamp with time zone`.
- Datas técnicas terminadas em `_em`.
- Restrições nomeadas explicitamente.
- Índices criados para chaves estrangeiras e campos usados em filtros.
- Enums persistidos como texto, nunca como posição ordinal.

## Campos de auditoria

As entidades persistentes devem utilizar, quando aplicável:

- `criado_em`: obrigatório e imutável.
- `atualizado_em`: obrigatório.
- `excluido_em`: nulo enquanto o registro estiver ativo.

## Usuários

Tabela: `usuarios`.

| Coluna          | Tipo         | Regra                      |
|-----------------|--------------|----------------------------|
| `id`            | UUID         | Chave primária             |
| `email`         | VARCHAR(254) | Obrigatório e único        |
| `telefone`      | VARCHAR(20)  | Opcional                   |
| `senha_hash`    | VARCHAR(255) | Obrigatório                |
| `ativo`         | BOOLEAN      | Obrigatório, padrão `true` |
| `criado_em`     | TIMESTAMPTZ  | Obrigatório                |
| `atualizado_em` | TIMESTAMPTZ  | Obrigatório                |
| `excluido_em`   | TIMESTAMPTZ  | Opcional                   |

O e-mail deve ser normalizado antes da persistência.

## Papéis

Tabela: `papeis`.

| Coluna | Tipo        | Regra               |
|--------|-------------|---------------------|
| `id`   | UUID        | Chave primária      |
| `nome` | VARCHAR(50) | Obrigatório e único |

Valores iniciais:

- `ADMINISTRADOR`
- `RESPONSAVEL_VAGA`
- `CANDIDATO`

Tabela associativa: `usuarios_papeis`.

| Coluna       | Tipo | Regra              |
|--------------|------|--------------------|
| `usuario_id` | UUID | FK para `usuarios` |
| `papel_id`   | UUID | FK para `papeis`   |

A chave primária deve ser composta por `usuario_id` e `papel_id`. Um usuário pode possuir mais de um papel.

## Candidatos

Tabela: `candidatos`.

| Coluna          | Tipo        | Regra                   |
|-----------------|-------------|-------------------------|
| `id`            | UUID        | Chave primária          |
| `usuario_id`    | UUID        | Obrigatório, FK e único |
| `data_admissao` | DATE        | Opcional                |
| `criado_em`     | TIMESTAMPTZ | Obrigatório             |
| `atualizado_em` | TIMESTAMPTZ | Obrigatório             |

O índice único em `usuario_id` garante que um usuário tenha no máximo um perfil de candidato.

## Currículos

Tabela: `curriculos`.

| Coluna            | Tipo         | Regra                             |
|-------------------|--------------|-----------------------------------|
| `id`              | UUID         | Chave primária                    |
| `candidato_id`    | UUID         | Obrigatório, FK para `candidatos` |
| `storage_key`     | VARCHAR(500) | Obrigatório e único               |
| `nome_original`   | VARCHAR(255) | Obrigatório                       |
| `content_type`    | VARCHAR(100) | Obrigatório                       |
| `tamanho_bytes`   | BIGINT       | Obrigatório e maior que zero      |
| `checksum_sha256` | CHAR(64)     | Obrigatório                       |
| `criado_em`       | TIMESTAMPTZ  | Obrigatório                       |
| `atualizado_em`   | TIMESTAMPTZ  | Obrigatório                       |
| `excluido_em`     | TIMESTAMPTZ  | Opcional                          |

Somente um currículo pode estar ativo por candidato. Essa regra deve ser garantida por índice único parcial considerando
registros cujo `excluido_em` seja nulo.

O arquivo não deve ser armazenado no PostgreSQL. O campo `storage_key` referencia o objeto mantido no bucket privado
`curriculos`.

Formato proposto da chave:

```text
curriculos/{candidatoId}/{curriculoId}.pdf
```

## Vagas

Tabela: `vagas`.

| Coluna           | Tipo         | Regra              |
|------------------|--------------|--------------------|
| `id`             | UUID         | Chave primária     |
| `responsavel_id` | UUID         | FK para `usuarios` |
| `titulo`         | VARCHAR(150) | Obrigatório        |
| `descricao`      | TEXT         | Obrigatório        |
| `status`         | VARCHAR(30)  | Obrigatório        |
| `criado_em`      | TIMESTAMPTZ  | Obrigatório        |
| `atualizado_em`  | TIMESTAMPTZ  | Obrigatório        |
| `excluido_em`    | TIMESTAMPTZ  | Opcional           |

Status propostos:

- `RASCUNHO`
- `PUBLICADA`
- `ENCERRADA`
- `CANCELADA`

## Perguntas das vagas

Tabela: `perguntas_vaga`.

| Coluna          | Tipo        | Regra                        |
|-----------------|-------------|------------------------------|
| `id`            | UUID        | Chave primária               |
| `vaga_id`       | UUID        | Obrigatório, FK para `vagas` |
| `enunciado`     | TEXT        | Obrigatório                  |
| `tipo_resposta` | VARCHAR(30) | Obrigatório                  |
| `obrigatoria`   | BOOLEAN     | Obrigatório                  |
| `ordem`         | INTEGER     | Obrigatório e maior que zero |
| `criado_em`     | TIMESTAMPTZ | Obrigatório                  |
| `atualizado_em` | TIMESTAMPTZ | Obrigatório                  |
| `excluido_em`   | TIMESTAMPTZ | Opcional                     |

Tipos inicialmente propostos:

- `TEXTO`
- `NUMERO`
- `BOOLEANO`
- `DATA`
- `SELECAO_UNICA`

## Requisitos das vagas

Tabela: `requisitos_vaga`.

| Coluna          | Tipo        | Regra                        |
|-----------------|-------------|------------------------------|
| `id`            | UUID        | Chave primária               |
| `vaga_id`       | UUID        | Obrigatório, FK para `vagas` |
| `descricao`     | TEXT        | Obrigatório                  |
| `obrigatorio`   | BOOLEAN     | Obrigatório                  |
| `criado_em`     | TIMESTAMPTZ | Obrigatório                  |
| `atualizado_em` | TIMESTAMPTZ | Obrigatório                  |
| `excluido_em`   | TIMESTAMPTZ | Opcional                     |

Critérios estruturados, como tempo mínimo de empresa, devem ser adicionados somente quando as regras de avaliação forem
definidas.

## Candidaturas

Tabela: `candidaturas`.

| Coluna          | Tipo        | Regra                             |
|-----------------|-------------|-----------------------------------|
| `id`            | UUID        | Chave primária                    |
| `candidato_id`  | UUID        | Obrigatório, FK para `candidatos` |
| `vaga_id`       | UUID        | Obrigatório, FK para `vagas`      |
| `status`        | VARCHAR(30) | Obrigatório                       |
| `criado_em`     | TIMESTAMPTZ | Obrigatório                       |
| `atualizado_em` | TIMESTAMPTZ | Obrigatório                       |
| `cancelado_em`  | TIMESTAMPTZ | Opcional                          |

Deve existir uma restrição única em `candidato_id` e `vaga_id`.

Status propostos:

- `ENVIADA`
- `EM_ANALISE`
- `APROVADA`
- `REJEITADA`
- `CANCELADA`

## Respostas das candidaturas

Tabela: `respostas_candidatura`.

| Coluna           | Tipo        | Regra                                 |
|------------------|-------------|---------------------------------------|
| `id`             | UUID        | Chave primária                        |
| `candidatura_id` | UUID        | Obrigatório, FK para `candidaturas`   |
| `pergunta_id`    | UUID        | Obrigatório, FK para `perguntas_vaga` |
| `valor`          | TEXT        | Obrigatório                           |
| `criado_em`      | TIMESTAMPTZ | Obrigatório                           |
| `atualizado_em`  | TIMESTAMPTZ | Obrigatório                           |

Deve existir uma restrição única em `candidatura_id` e `pergunta_id`.

## Refresh tokens

Tabela: `refresh_tokens`.

| Coluna        | Tipo         | Regra                           |
|---------------|--------------|---------------------------------|
| `id`          | UUID         | Chave primária                  |
| `usuario_id`  | UUID         | Obrigatório, FK para `usuarios` |
| `token_hash`  | VARCHAR(255) | Obrigatório e único             |
| `familia_id`  | UUID         | Obrigatório                     |
| `expira_em`   | TIMESTAMPTZ  | Obrigatório                     |
| `usado_em`    | TIMESTAMPTZ  | Opcional                        |
| `revogado_em` | TIMESTAMPTZ  | Opcional                        |
| `criado_em`   | TIMESTAMPTZ  | Obrigatório                     |

O token em texto puro nunca deve ser persistido.

## Templates

Tabelas:

- `templates_vaga`
- `perguntas_template_vaga`
- `requisitos_template_vaga`

Os campos devem acompanhar os equivalentes de vaga, pergunta e requisito. Ao criar uma vaga a partir de um template,
seus dados devem ser copiados para preservar o histórico.

## Notificações

Tabela proposta: `notificacoes`.

Campos mínimos:

- `id`
- `usuario_id`
- `tipo`
- `titulo`
- `mensagem`
- `lida_em`
- `criado_em`

O canal definitivo ainda precisa ser confirmado.
