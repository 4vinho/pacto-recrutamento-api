# Preparação para implementação

## Objetivo

Consolidar as decisões técnicas necessárias antes da implementação do backend.

Esta pasta contém somente planejamento. Nenhuma das dependências, migrations, entidades ou classes descritas aqui foi implementada.

## Documentos

1. [Decisões pendentes](decisoes-pendentes.md)
2. [Modelo de dados](modelo-de-dados.md)
3. [Flyway e migrations](flyway-e-migrations.md)
4. [Autenticação JWT](autenticacao-jwt.md)
5. [Arquitetura Clean/Onion](arquitetura-clean-onion.md)
6. [Armazenamento de currículos](armazenamento-de-curriculos.md)
7. [Contratos, DTOs e respostas](contratos-dtos-e-respostas.md)
8. [Testes e ordem de implementação](testes-e-ordem-de-implementacao.md)
9. [Guia de implementação](guid-impl.md)

## Premissas iniciais

- Java 8.
- Spring Boot 2.7.18.
- PostgreSQL.
- Identificadores UUID.
- Tabelas e colunas em português, no plural e em `snake_case`.
- Datas armazenadas em UTC.
- Exclusão lógica nos recursos que precisam preservar histórico.
- Access token JWT com duração de 15 minutos.
- Refresh token rotativo e persistido de forma segura.
- Papéis `ADMINISTRADOR`, `RESPONSAVEL_VAGA` e `CANDIDATO`.
- Organização em `core`, `app`, `infra` e `web`.
- Currículos em PDF armazenados em bucket privado no MinIO.
- Metadados dos currículos armazenados no PostgreSQL.

As premissas devem ser confirmadas antes da implementação.
