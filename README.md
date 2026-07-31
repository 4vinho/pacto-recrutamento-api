# Recrutamento API

Backend do sistema de recrutamento interno.

## Requisitos

- Docker e Docker Compose

Para execução sem Docker:

- Java 8
- Maven 3.8+
- PostgreSQL

## Executar com Docker

Copie `.env.example` para `.env` e altere as credenciais e a chave JWT:

```shell
cp .env.example .env
```

No PowerShell:

```powershell
Copy-Item .env.example .env
```

```shell
docker compose up --build
```

Durante o desenvolvimento, execute o Compose Watch para reconstruir e recriar a API
automaticamente quando houver alterações em `src` ou no `pom.xml`:

```shell
docker compose watch
```

Serviços:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- PostgreSQL: `localhost:5432`
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`

Os dados do PostgreSQL e do MinIO são mantidos em volumes nomeados.

## Executar sem Docker

Defina obrigatoriamente `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`,
`MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET`, `SERVER_PORT`
e `JWT_SECRET` antes de iniciar a aplicação.
Depois execute:

```shell
mvn spring-boot:run
```

## Testar

```shell
mvn test
```
