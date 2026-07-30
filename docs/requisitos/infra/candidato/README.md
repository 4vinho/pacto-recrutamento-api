# Infra: candidato

- Repositório JPA implementa a porta de perfil do candidato.
- `usuario_id` possui FK e índice único.
- Consultas do próprio perfil devem filtrar pelo usuário autenticado.
- Mapeamentos JPA não são expostos para `core`, `app` ou respostas HTTP.
