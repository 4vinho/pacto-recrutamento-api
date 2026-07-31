# Endpoints: currículo

| Método | Rota                           | Consumo               | Finalidade               |
|--------|--------------------------------|-----------------------|--------------------------|
| POST   | `/candidatos/me/curriculo`     | `multipart/form-data` | Enviar ou substituir PDF |
| GET    | `/candidatos/me/curriculo/url` | —                     | Gerar URL temporária     |

O upload aceita um arquivo, limita 5 MB e retorna os metadados, nunca o binário
ou credenciais do storage. A rota de URL é proposta e pode ser ajustada à tela.
