# Infra: candidatura

- Repositórios JPA para candidatura e respostas.
- Restrição única `(candidato_id, vaga_id)`.
- Restrição única `(candidatura_id, pergunta_id)`.
- Registro do lote de respostas participa de uma transação PostgreSQL.
- Consultas de painel evitam N+1 e retornam projeções próprias.
- Violação concorrente de unicidade é traduzida em conflito de negócio.
