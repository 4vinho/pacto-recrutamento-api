# Notificações e segurança operacional

## Notificações

O disparo por eventos e a persistência existem, mas o canal final é simulado.
Para uma entrega produtiva faltam:

- integração real de e-mail, mensageria ou outro canal;
- retentativa e tratamento durável de falhas (por exemplo, outbox);
- endpoint para o usuário listar ou marcar notificações como lidas, caso a
  interface de notificações faça parte do produto.

## Segurança operacional

Como endurecimento para produção, ainda não foram identificados:

- rate limiting ou bloqueio progressivo no login/recuperação;
- configuração CORS explícita por ambiente;
- propriedades externas para todos os tempos de expiração de tokens;
- correlação de requisições e política estruturada de auditoria.

Esses itens são melhorias de produção; autenticação e autorização básicas estão
entregues.
