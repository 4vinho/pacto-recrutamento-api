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
