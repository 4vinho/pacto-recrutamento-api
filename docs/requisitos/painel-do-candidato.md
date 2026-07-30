# Painel do candidato

## Objetivo

Permitir que o candidato acompanhe o status de suas candidaturas e consulte os feedbacks recebidos.

## Status da candidatura

O status pertence à candidatura, e não ao candidato, pois um candidato pode estar em etapas diferentes em cada vaga.

Os status inicialmente previstos são:

- `ENVIADA`
- `EM_ANALISE`
- `APROVADA`
- `REJEITADA`
- `CANCELADA`

```text
Candidatura N --- 1 CandidaturaStatus
```

## Regras de negócio

- O candidato deve visualizar somente as próprias candidaturas.
- Cada candidatura deve apresentar a vaga, o status atual e a data da inscrição.
- Quando disponível, o candidato deve conseguir consultar o feedback relacionado à candidatura.

## Critérios de aceitação

- Dado um candidato autenticado, quando acessar o painel, então suas candidaturas devem ser apresentadas.
- Dado que uma candidatura mudou de etapa, quando o painel for consultado, então o status atual deve ser exibido.
- Dado que existe feedback para uma candidatura, quando ela for consultada, então o feedback deve ser apresentado.
