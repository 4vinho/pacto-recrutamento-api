# Entidade: candidatura

## Responsabilidade

Representar a inscrição de um candidato em uma vaga e suas respostas.

## Estrutura

- `candidaturas`: candidato, vaga, status, criação, atualização e cancelamento.
- `respostas_candidatura`: candidatura, pergunta, valor e auditoria.

Status: `ENVIADA`, `EM_ANALISE`, `APROVADA`, `REJEITADA`, `CANCELADA`.

## Invariantes

- `UNIQUE (candidato_id, vaga_id)`.
- Nova candidatura começa como `ENVIADA`.
- O status pertence à candidatura, não ao candidato.
- `UNIQUE (candidatura_id, pergunta_id)`.
- Cada resposta referencia pergunta da mesma vaga e respeita seu tipo.
- Um lote inválido não produz persistência parcial.
- Somente o candidato proprietário cancela; somente responsável autorizado
  altera a etapa.
