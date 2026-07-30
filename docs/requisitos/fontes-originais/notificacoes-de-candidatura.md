# Notificações de candidatura

## Objetivo

Notificar os envolvidos quando uma candidatura for criada ou tiver seu status alterado.

## Eventos e comportamentos internos

As notificações não precisam ser expostas como endpoints públicos. Elas devem ser disparadas pelos eventos do domínio:

```text
CandidaturaCriada
StatusCandidaturaAlterado
```

Os comportamentos esperados são:

- `NotificarResponsavelSobreNovaCandidatura`.
- `NotificarCandidatoSobreNovaCandidatura`.
- `NotificarCandidatoSobreAlteracaoDeStatus`.

## Regras de negócio

- A criação de uma candidatura deve notificar o responsável pela vaga e o candidato.
- A alteração do status deve notificar o candidato.
- Uma falha no envio deve ser registrada para permitir nova tentativa.
- O conteúdo da notificação deve identificar a vaga e a candidatura.

## Critérios de aceitação

- Dada uma candidatura criada com sucesso, então o responsável pela vaga e o candidato devem ser notificados.
- Dado que o status da candidatura foi alterado, então o candidato deve ser notificado.
- Dada uma falha no envio, então a ocorrência deve ser registrada para reprocessamento.
