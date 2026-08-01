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

## Endpoints principais

- `GET /candidaturas/me`
- `POST /vagas/{vagaId}/candidaturas`
- `POST /candidaturas/{id}/respostas`
- `POST /candidaturas/{id}/requisitos`
- `GET /vagas/{vagaId}/candidaturas`
- `GET /candidaturas/{id}`
- `PATCH /candidaturas/{id}/status`
- `POST /candidaturas/{id}/cancelamento`

## Evidências principais

`CandidaturaController`, `CandidaturaService`, adapters e repositórios de
candidatura, perguntas e requisitos, além das migrations `V7`, `V15` e `V16`.
