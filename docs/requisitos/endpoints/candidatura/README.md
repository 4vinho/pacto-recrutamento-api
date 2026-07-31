# Endpoints: candidatura

| Método | Rota                                         | Acesso                              |
|--------|----------------------------------------------|-------------------------------------|
| POST   | `/vagas/{vagaId}/candidaturas`               | Candidato                           |
| POST   | `/candidaturas/{candidaturaId}/respostas`    | Candidato proprietário              |
| POST   | `/candidaturas/{candidaturaId}/requisitos`   | Candidato proprietário              |
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

Formato da autoavaliação dos requisitos:

```json
{
  "respostas": [
    { "requisitoId": "uuid", "nivel": "ALTO" }
  ]
}
```

Os níveis aceitos são `MUITO_BAIXO`, `BAIXO`, `ALTO` e `MUITO_ALTO`.
Perguntas e requisitos podem ser respondidos em qualquer ordem. A candidatura
permanece como `RASCUNHO` e somente passa para `ENVIADA` depois que as duas
etapas aplicáveis forem concluídas.
