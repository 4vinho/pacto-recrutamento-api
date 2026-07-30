# Services: candidatura

## Casos de uso

- `CriarCandidatura`: valida candidato e vaga, impede duplicidade e cria como `ENVIADA`.
- `RegistrarRespostas`: valida coleção completa e grava em uma transação.
- `AtualizarStatusCandidatura`: valida responsável e transição.
- `CancelarCandidatura`: valida propriedade e estado atual.
- `ConsultarCandidatura` e `ListarMinhasCandidaturas`: protegem dados por proprietário.

## Atomicidade e concorrência

- Respostas duplicadas, de outra vaga ou incompatíveis invalidam todo o lote.
- A restrição única do banco é a proteção final contra candidaturas concorrentes.
- Criação e mudança de status publicam eventos somente após sucesso do fluxo.

Feedback e avaliação estruturada são bônus e permanecem pendentes de regra.
