# Endpoints: usuário

| Método | Rota | Acesso | Finalidade |
|---|---|---|---|
| POST | `/auth/cadastro` | Público | Cadastrar usuário |
| POST | `/auth/login` | Público | Autenticar e emitir tokens |
| POST | `/auth/refresh` | Refresh válido | Rotacionar sessão |
| POST | `/auth/logout` | Autenticado | Encerrar sessão |
| POST | `/auth/recuperacao-senha/solicitacoes` | Público | Solicitar recuperação |
| POST | `/auth/recuperacao-senha/confirmacoes` | Token temporário | Redefinir senha |

Cadastro retorna `201`; login/refresh retornam tokens; credenciais inválidas
retornam `401`; papel insuficiente retorna `403`. A solicitação de recuperação
usa a mesma resposta exista ou não o e-mail.
