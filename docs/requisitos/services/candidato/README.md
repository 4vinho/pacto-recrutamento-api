# Services: candidato

## Casos de uso

- `CriarCandidato`: associa um perfil ao usuário autenticado.
- `AtualizarCandidato`: atualiza somente o próprio perfil.
- `ListarMinhasCandidaturas`: retorna vaga, status, data e feedback disponível.

## Resultado esperado

- O usuário não cria dois perfis.
- Um candidato não lê ou altera dados privados de outro.
- A consulta do painel reflete sempre o estado atual de cada candidatura.
