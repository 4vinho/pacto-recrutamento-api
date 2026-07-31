# Services: notificação

## Eventos consumidos

- `CandidaturaCriada`.
- `StatusCandidaturaAlterado`.

## Comportamentos

- Notificar responsável e candidato sobre nova candidatura.
- Notificar candidato sobre alteração de status.
- Persistir a intenção de envio antes de integrar com o canal.
- Registrar erro e permitir nova tentativa idempotente.

Falha de notificação não deve desfazer uma candidatura já confirmada.

Os eventos são consumidos após o commit da candidatura. No ambiente atual, o
canal é simulado e registra destinatário, tipo, título e mensagem no log da API
com o prefixo `[SIMULADO]`.
