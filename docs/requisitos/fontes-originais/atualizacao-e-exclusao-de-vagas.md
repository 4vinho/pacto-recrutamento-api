# Atualização e exclusão de vagas

## Objetivo

Permitir que administradores mantenham vagas, perguntas, requisitos e templates existentes.

## Comandos e endpoints

### Vaga

```http
PUT    /vagas/{vagaId}
PATCH  /vagas/{vagaId}/status
DELETE /vagas/{vagaId}
```

Os casos de uso correspondentes são:

- `AtualizarVaga`.
- `AlterarStatusVaga`.
- `ExcluirVaga`.

### Perguntas da vaga

```http
PUT    /vagas/{vagaId}/perguntas/{perguntaId}
DELETE /vagas/{vagaId}/perguntas/{perguntaId}
```

Os casos de uso correspondentes são:

- `AtualizarPerguntaDaVaga`.
- `ExcluirPerguntaDaVaga`.

### Requisitos da vaga

```http
PUT    /vagas/{vagaId}/requisitos/{requisitoId}
DELETE /vagas/{vagaId}/requisitos/{requisitoId}
```

Os casos de uso correspondentes são:

- `AtualizarRequisitoDaVaga`.
- `ExcluirRequisitoDaVaga`.

### Template de vaga

```http
PUT    /templates-vaga/{templateId}
DELETE /templates-vaga/{templateId}
```

Os casos de uso correspondentes são:

- `AtualizarTemplateDeVaga`.
- `ExcluirTemplateDeVaga`.

### Perguntas do template

```http
PUT    /templates-vaga/{templateId}/perguntas/{perguntaId}
DELETE /templates-vaga/{templateId}/perguntas/{perguntaId}
```

Os casos de uso correspondentes são:

- `AtualizarPerguntaDoTemplate`.
- `ExcluirPerguntaDoTemplate`.

### Requisitos do template

```http
PUT    /templates-vaga/{templateId}/requisitos/{requisitoId}
DELETE /templates-vaga/{templateId}/requisitos/{requisitoId}
```

Os casos de uso correspondentes são:

- `AtualizarRequisitoDoTemplate`.
- `ExcluirRequisitoDoTemplate`.

## Regras de negócio

- Apenas administradores podem executar essas operações.
- O status da vaga deve ser alterado por uma operação específica.
- A exclusão deve ser lógica, preservando o histórico e os relacionamentos existentes.
- Um item excluído logicamente não deve aceitar novas alterações.
- Perguntas e requisitos devem pertencer à vaga ou ao template informado na rota.

## Critérios de aceitação

- Dado um administrador autenticado, quando atualizar uma vaga válida, então as alterações devem ser registradas.
- Dado um usuário sem permissão administrativa, quando tentar modificar uma vaga, então a operação deve ser rejeitada.
- Dado um recurso válido, quando sua exclusão for solicitada, então ele deve ser marcado como excluído sem remoção
  física.
- Dado um recurso que não pertence ao pai informado na rota, quando uma alteração for solicitada, então a operação deve
  ser rejeitada.
