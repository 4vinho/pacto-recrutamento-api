# Endpoints: vaga

| Método | Rota                                       |
|--------|--------------------------------------------|
| POST   | `/vagas`                                   |
| PUT    | `/vagas/{vagaId}`                          |
| PATCH  | `/vagas/{vagaId}/status`                   |
| DELETE | `/vagas/{vagaId}`                          |
| POST   | `/vagas/{vagaId}/perguntas`                |
| PUT    | `/vagas/{vagaId}/perguntas/{perguntaId}`   |
| DELETE | `/vagas/{vagaId}/perguntas/{perguntaId}`   |
| POST   | `/vagas/{vagaId}/requisitos`               |
| PUT    | `/vagas/{vagaId}/requisitos/{requisitoId}` |
| DELETE | `/vagas/{vagaId}/requisitos/{requisitoId}` |

Manutenção exige papel administrativo/autorizado. Pai e filho incompatíveis
retornam `404` ou `422`, conforme a convenção escolhida. Listagens e detalhes
públicos serão definidos a partir das telas.
