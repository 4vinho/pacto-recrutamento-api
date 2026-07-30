# Contrato HTTP comum

## Envelope

Respostas usam um envelope consistente com dados, mensagem e erros de validação.
Respostas sem corpo podem usar `204`.

## Status

- `200`: consulta/alteração concluída.
- `201`: recurso criado.
- `204`: exclusão lógica ou operação sem representação.
- `400`: JSON/formato inválido.
- `401`: não autenticado.
- `403`: autenticado sem permissão.
- `404`: recurso não encontrado no escopo autorizado.
- `409`: conflito de unicidade/estado concorrente.
- `422`: regra de negócio ou validação semântica.

## Regras

- DTOs de entrada e saída são separados das entidades JPA e de domínio.
- IDs de proprietário vêm da autenticação, não do corpo.
- Erros não expõem stack trace, SQL, tokens ou dados sensíveis.
- Validação em lote identifica itens quando útil, preservando atomicidade.
