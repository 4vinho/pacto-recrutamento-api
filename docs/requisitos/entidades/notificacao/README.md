# Entidade: notificação

## Responsabilidade

Registrar uma comunicação destinada a um usuário e permitir reprocessamento.

## Estrutura mínima proposta

Tabela `notificacoes`: id, usuário, tipo, título, mensagem, estado/tentativas,
data de leitura, criação e último erro.

## Invariantes

- A mensagem identifica vaga e candidatura.
- Falha de envio permanece registrada para nova tentativa.
- Dados sensíveis não devem fazer parte da mensagem ou dos logs.
- O canal definitivo de envio permanece pendente; e-mail pode ser simulado.
