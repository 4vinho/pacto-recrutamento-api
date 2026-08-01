# Candidaturas e avaliação

## Candidatura entregue

- Criação de candidatura vinculada ao usuário autenticado e a uma vaga.
- Rascunho de candidatura e registro separado de respostas a perguntas e de
  autoavaliações dos requisitos.
- Consulta detalhada, cancelamento e listagem paginada das próprias
  candidaturas.
- Bloqueios de negócio para candidatura duplicada e transições inválidas.

## Avaliação entregue no backend

- Listagem paginada das candidaturas de uma vaga, com filtro por status.
- Consulta do detalhe de uma candidatura por usuário autorizado.
- Alteração de status pelo responsável autorizado pela vaga.
- Estados do processo modelados no domínio e validação das transições.
- Resumo do painel com totais agregados por status e filtro por período.
- Filtros servidor por status, nível mínimo de atendimento dos requisitos e
  tempo mínimo de empresa calculado pela data de admissão.
- Feedback textual em cada avaliação e histórico auditável de transições.
- Controle de concorrência com `@Version` e versão obrigatória no contrato HTTP.

## Endpoints principais

- `GET /candidaturas/me`
- `POST /vagas/{vagaId}/candidaturas`
- `POST /candidaturas/{id}/respostas`
- `POST /candidaturas/{id}/requisitos`
- `GET /vagas/{vagaId}/candidaturas`
- `GET /candidaturas/{id}`
- `PATCH /candidaturas/{id}/status`
- `GET /candidaturas/me/resumo`
- `POST /candidaturas/{id}/cancelamento`

## Evidências principais

`CandidaturaController`, `CandidaturaService`, adapters e repositórios de
candidatura, perguntas e requisitos, além das migrations `V7`, `V15` e `V16`.
