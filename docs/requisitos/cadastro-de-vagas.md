# Cadastro de vagas

## Objetivo

Permitir que administradores cadastrem vagas com título, descrição, requisitos e perguntas para os candidatos.

## Modelo de domínio

### Vaga

Cada vaga deve possuir:

- Título.
- Descrição.
- Status.
- Requisitos.
- Perguntas, quando necessárias.

```text
Vaga N --- 1 VagaStatus
Vaga 1 --- 0..N RequisitoVaga
Vaga 1 --- 0..N PerguntaVaga
```

### Pergunta da vaga

Cada pergunta pertence a exatamente uma vaga e deve informar:

- Texto.
- Tipo de resposta.
- Se a resposta é obrigatória.
- Ordem de apresentação.

## Comandos e endpoints

### Criar vaga

```http
POST /vagas
```

### Criar pergunta da vaga

```http
POST /vagas/{vagaId}/perguntas
```

### Criar requisito da vaga

```http
POST /vagas/{vagaId}/requisitos
```

### Criar template de vaga

```http
POST /templates-vaga
```

### Criar pergunta do template

```http
POST /templates-vaga/{templateId}/perguntas
```

### Criar requisito do template

```http
POST /templates-vaga/{templateId}/requisitos
```

## Regras de negócio

- Apenas administradores podem cadastrar vagas.
- Uma vaga deve possuir título e descrição.
- O status da vaga determina se ela pode receber candidaturas.
- Perguntas marcadas como obrigatórias devem ser respondidas no momento da candidatura.

## Critérios de aceitação

- Dado um administrador autenticado, quando informar os dados obrigatórios, então uma vaga deve ser cadastrada.
- Dado um usuário sem permissão administrativa, quando tentar cadastrar uma vaga, então a operação deve ser rejeitada.
- Dado que faltam título ou descrição, quando o cadastro for solicitado, então a operação deve ser rejeitada.
