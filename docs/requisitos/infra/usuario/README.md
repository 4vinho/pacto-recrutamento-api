# Infra: usuário

- Repositórios JPA para usuários, papéis e refresh tokens.
- BCrypt para hash de senha.
- Biblioteca JWT compatível com Java 8; assinatura e chaves por configuração.
- Filtro Spring Security para autenticação e autorização por papel.
- Refresh e recuperação persistidos somente como hash.
- Índices únicos para e-mail e token hash.
- Logs nunca incluem senha, token ou credenciais.
