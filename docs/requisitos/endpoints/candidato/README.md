# Endpoints: candidato

| Método | Rota                          | Finalidade                          |
|--------|-------------------------------|-------------------------------------|
| POST   | `/candidatos`                 | Criar perfil do usuário autenticado |
| PUT    | `/candidatos/me`              | Atualizar o próprio perfil          |
| GET    | `/candidatos/me/candidaturas` | Exibir painel do candidato          |

As rotas exigem autenticação e nunca recebem o usuário proprietário pelo corpo.
Filtros/paginação do painel serão definidos com a tela.
