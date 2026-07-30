# Infra: notificação

- Repositório persiste notificações e tentativas de envio.
- Adaptador de canal fica atrás de uma porta e pode começar como simulação.
- Processamento deve ser idempotente.
- Falhas registram motivo sanitizado e próxima tentativa.
- O mecanismo de fila/outbox é proposto quando for necessário garantir entrega.
