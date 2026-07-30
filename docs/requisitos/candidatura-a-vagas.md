# Candidatura a vagas

## Objetivo

Permitir que um candidato demonstre interesse em uma vaga disponível.

## Modelo de domínio

### Candidato

Representa o perfil profissional do candidato e armazena informações como o currículo.

Um candidato pode realizar candidaturas para várias vagas.

```text
Candidato 1 --- 0..N Candidatura
```

### Candidatura

Representa a inscrição de um candidato em uma vaga específica.

Cada candidatura deve:

- Pertencer a exatamente um candidato.
- Pertencer a exatamente uma vaga.
- Possuir um status.
- Armazenar as respostas fornecidas pelo candidato às perguntas da vaga.
- Registrar a data e a hora em que foi realizada.

```text
Candidatura N --- 1 Candidato
Candidatura N --- 1 Vaga
Candidatura N --- 1 CandidaturaStatus
```

O sistema não deve permitir mais de uma candidatura do mesmo candidato para a mesma vaga.

```text
UNIQUE (candidato_id, vaga_id)
```

### Respostas da candidatura

Cada resposta deve pertencer a uma candidatura e a uma pergunta da vaga.

```text
Candidatura 1 --- 0..N RespostaCandidatura
PerguntaVaga 1 --- 0..N RespostaCandidatura
```

Uma candidatura não pode possuir mais de uma resposta para a mesma pergunta.

```text
UNIQUE (candidatura_id, pergunta_id)
```

## Comandos e endpoints

### Criar candidato

```http
POST /candidatos
```

Cria o perfil profissional associado ao usuário autenticado.

### Atualizar candidato

```http
PUT /candidatos/me
```

Atualiza os dados profissionais do próprio candidato.

### Criar candidatura

```http
POST /vagas/{vagaId}/candidaturas
```

Cria uma candidatura do candidato autenticado para a vaga informada.

### Responder perguntas da candidatura

```http
POST /candidaturas/{candidaturaId}/respostas
```

O endpoint deve receber uma coleção contendo uma ou mais respostas:

```json
{
  "respostas": [
    {
      "perguntaId": 1,
      "valor": "Resposta do candidato"
    },
    {
      "perguntaId": 2,
      "valor": "Outra resposta"
    }
  ]
}
```

A coleção deve conter pelo menos uma resposta. Todas as respostas recebidas na mesma requisição devem ser processadas em uma única transação: se uma delas for inválida, nenhuma deve ser registrada.

### Atualizar status da candidatura

```http
PATCH /candidaturas/{candidaturaId}/status
```

Atualiza a etapa da candidatura. A operação deve ser restrita aos usuários responsáveis pelo processo seletivo.

### Cancelar candidatura

```http
POST /candidaturas/{candidaturaId}/cancelamento
```

Permite que o candidato cancele a própria candidatura, quando o estado atual permitir.

## Regras de negócio

- A vaga deve estar aberta para receber candidaturas.
- O candidato não pode se candidatar mais de uma vez à mesma vaga.
- Uma nova candidatura deve ser criada com o status `ENVIADA`.
- O status e as respostas devem estar associados à candidatura, nunca diretamente ao candidato.
- O envio de respostas deve conter pelo menos um item.
- Cada pergunta deve pertencer à vaga da candidatura.
- Uma mesma pergunta não pode aparecer mais de uma vez na requisição.
- Uma resposta não deve ser registrada se qualquer outra resposta da mesma requisição for inválida.

## Critérios de aceitação

- Dado um candidato autorizado, quando ele se candidatar a uma vaga aberta, então uma candidatura deve ser registrada.
- Dado que o candidato já se candidatou a uma vaga, quando tentar se candidatar novamente à mesma vaga, então a operação deve ser rejeitada.
- Dado que a candidatura foi registrada com sucesso, então seu status inicial deve ser `ENVIADA`.
- Dada uma candidatura válida, quando uma ou mais respostas válidas forem enviadas, então todas devem ser registradas.
- Dado que uma das respostas enviadas é inválida, quando a operação for processada, então nenhuma resposta da requisição deve ser registrada.
