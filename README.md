# Pacto Recrutamento — API

API REST do portal interno de recrutamento. Implementada em Java 8 e Spring Boot
2.7, com PostgreSQL, Flyway, MinIO, JWT e envio de e-mail por SMTP.

## Funcionalidades

- cadastro, autenticação, renovação e encerramento de sessão;
- recuperação de senha;
- vagas com perguntas, requisitos e múltiplos responsáveis;
- templates reutilizáveis para criação de vagas;
- candidatura em etapas, currículo e acompanhamento do processo seletivo;
- avaliação com filtros, histórico, feedback e controle de concorrência;
- notificações persistidas, envio por e-mail e retentativa automática.

O detalhamento de arquitetura, regras, endpoints, fluxos e banco está em
[DOCUMENTACAO.md](DOCUMENTACAO.md). O contrato completo e executável fica no
Swagger.

## Requisitos

Para executar toda a solução: Docker e Docker Compose. Para executar somente a
API localmente: Java 8, Maven 3.8+, PostgreSQL, MinIO e um servidor SMTP.

Os repositórios `pacto-recrutamento-api` e `pacto-recrutamento-web` devem estar
lado a lado para o Compose iniciar a solução completa.

## Executar com Docker

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Serviços disponíveis:

| Serviço | Endereço |
| --- | --- |
| Frontend | `http://localhost:4200` |
| API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Health check | `http://localhost:8080/actuator/health` |
| PostgreSQL | `localhost:5432` |
| MinIO API / console | `http://localhost:9000` / `http://localhost:9001` |
| Mailpit | `http://localhost:8025` |

O PostgreSQL e o MinIO usam volumes persistentes. Para parar sem apagar os dados,
execute `docker compose down`. `docker compose down --volumes` também remove os
dados locais. Para observar e reconstruir alterações da API, use
`docker compose watch`.

## Executar somente a API

O perfil padrão exige estas variáveis:

- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`;
- `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET`;
- `SERVER_PORT` e `JWT_SECRET`;
- `MAIL_HOST` e `MAIL_PORT`.

As demais opções de e-mail e retentativa têm padrões em `application.yml`. Para
desenvolvimento, o perfil `dev` fornece padrões locais para banco, MinIO, porta e
JWT:

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Contas de demonstração

Criadas pelo Flyway em um banco novo:

| Papel | E-mail | Senha |
| --- | --- | --- |
| Administrador | `socrates@pacto.com` | `socrates` |
| Responsável por vaga | `platao@pacto.com` | `platao` |
| Candidato | `aristoteles@pacto.com` | `aristoteles` |

Use essas credenciais somente em desenvolvimento.

## Verificação

```powershell
mvn test
mvn verify
```

`verify` também gera a cobertura JaCoCo em `target/site/jacoco/`. As instruções de
análise estática estão em [SONARQUBE.md](SONARQUBE.md).

## Convenções operacionais

- respostas usam o envelope `TypedResponse`; listas paginadas usam
  `TypedPagedResponse`;
- endpoints protegidos recebem `Authorization: Bearer <access-token>`;
- migrations ficam em `src/main/resources/db/migration` e migrations aplicadas
  nunca devem ser alteradas;
- currículos aceitam até 5 MiB e os arquivos ficam no MinIO; o banco mantém apenas
  seus metadados;
- segredos e o arquivo `.env` não devem ser versionados.
