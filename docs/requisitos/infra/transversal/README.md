# Infra: requisitos transversais

## Banco e migrations

- PostgreSQL e Flyway; migrations versionadas, imutáveis após compartilhadas e
  testadas do zero.
- UUID, `TIMESTAMPTZ`, enums textuais, auditoria e restrições nomeadas.

## Aplicação e containers

- Docker Compose com PostgreSQL, MinIO e API; frontend quando conteinerizado.
- Imagens com versão fixa, health checks, volumes e segredos fora das imagens.
- Relógio UTC injetável.

## Qualidade

- Unitários: domínio, status, casos de uso, lotes e rotação de tokens.
- Integração: JPA, Flyway, PostgreSQL, MinIO, segurança e transações.
- Web: JSON, validação, status HTTP e autorização.
- ArchUnit: dependências entre `core`, `app`, `infra` e `web`.
