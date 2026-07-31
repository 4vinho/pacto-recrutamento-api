# Endpoints: candidatura

| Método | Rota                                         | Acesso                              |
|--------|----------------------------------------------|-------------------------------------|
| POST   | `/vagas/{vagaId}/candidaturas`               | Candidato                           |
| POST   | `/candidaturas/{candidaturaId}/respostas`    | Candidato proprietário              |
| PATCH  | `/candidaturas/{candidaturaId}/status`       | Responsável autorizado              |
| POST   | `/candidaturas/{candidaturaId}/cancelamento` | Candidato proprietário              |
| GET    | `/candidaturas/{candidaturaId}`              | Proprietário/responsável autorizado |

Formato do lote:

```json
{
  "respostas": [
    { "perguntaId": "uuid", "valor": "Resposta" }
  ]
}
```

A lista exige ao menos um item e é processada atomicamente. Candidatura
duplicada retorna `409`; transição inválida retorna `422`.
