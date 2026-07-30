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

## Regras de negócio

- A vaga deve estar aberta para receber candidaturas.
- O candidato não pode se candidatar mais de uma vez à mesma vaga.
- As perguntas obrigatórias da vaga devem ser respondidas.
- Uma nova candidatura deve ser criada com o status `ENVIADA`.
- O status e as respostas devem estar associados à candidatura, nunca diretamente ao candidato.

## Critérios de aceitação

- Dado um candidato autorizado, quando ele se candidatar a uma vaga aberta, então uma candidatura deve ser registrada.
- Dado que o candidato já se candidatou a uma vaga, quando tentar se candidatar novamente à mesma vaga, então a operação deve ser rejeitada.
- Dado que uma vaga possui perguntas obrigatórias, quando alguma delas não for respondida, então a candidatura não deve ser registrada.
- Dado que a candidatura foi registrada com sucesso, então seu status inicial deve ser `ENVIADA`.
