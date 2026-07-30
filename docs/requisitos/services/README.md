# Requisitos de services

Services representam casos de uso da camada `app`. Eles coordenam domínio,
transações e portas; não conhecem controller, JPA ou SDK externo.

- [Usuário e autenticação](usuario/README.md)
- [Perfil do candidato](candidato/README.md)
- [Currículo](curriculo/README.md)
- [Vaga](vaga/README.md)
- [Candidatura](candidatura/README.md)
- [Template de vaga](template-vaga/README.md)
- [Notificação](notificacao/README.md)

Cada caso de uso deve possuir comando de entrada, resultado explícito, testes
unitários e fronteira transacional própria.
