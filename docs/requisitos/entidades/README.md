# Requisitos de entidades

Define o modelo de domínio, as invariantes e os mapeamentos JPA das entidades,
sem detalhes de controller ou integrações externas.

- [Usuário](usuario/README.md)
- [Candidato](candidato/README.md)
- [Currículo](curriculo/README.md)
- [Vaga, perguntas e requisitos](vaga/README.md)
- [Candidatura e respostas](candidatura/README.md)
- [Template de vaga](template-vaga/README.md)
- [Notificação](notificacao/README.md)

## Convenções persistentes

- Tabelas e colunas em português, no plural e em `snake_case`.
- Chaves primárias `id`; estrangeiras terminadas em `_id`.
- Campos técnicos: `criado_em`, `atualizado_em` e, quando aplicável, `excluido_em`.
- Índices em chaves estrangeiras e campos usados em filtros.
- Restrições de unicidade devem existir também no banco.
