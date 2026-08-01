# Infraestrutura, documentação e qualidade

## Entregue

- Java 8 com Spring Boot 2.7, JPA, PostgreSQL e Flyway.
- Arquitetura em `core`, `app`, `infra` e `web`, com portas e adapters e testes
  que protegem a direção das dependências.
- Docker Compose para API, PostgreSQL e MinIO, volumes persistentes, healthcheck
  e arquivo `.env.example` sem segredos reais.
- OpenAPI/Swagger e endpoint Actuator de saúde.
- Resposta HTTP tipada, paginação comum e tratamento global de exceções.
- JaCoCo no ciclo `verify` e configuração do scanner Sonar Maven.
- Testes de domínio, casos de uso, adapters, segurança, storage, mapeamento JPA
  e regras arquiteturais.
- README com execução por Docker e Maven, serviços, variáveis obrigatórias,
  contas iniciais e comando de testes.
- Documentação detalhada dos requisitos e contratos em `docs/requisitos`.

## Observação

A presença dos mecanismos acima não significa cobertura total. As lacunas de
teste integrado, documentação operacional e robustez estão catalogadas em
`../faltas/qualidade-e-documentacao.md`.
