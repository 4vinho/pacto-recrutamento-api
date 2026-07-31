# Recrutamento API

Backend do sistema de recrutamento interno.

## Requisitos

- Docker e Docker Compose

Para execução sem Docker:

- Java 8
- Maven 3.8+
- PostgreSQL

## Executar com Docker

Opcionalmente, copie `.env.example` para `.env` e altere as configurações locais.

```shell
docker compose up --build
```

Serviços:

- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`

Os dados do PostgreSQL e do MinIO são mantidos em volumes nomeados.

## Executar sem Docker

Configure `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` se os valores padrão não atenderem ao seu ambiente.
Depois execute:

```shell
mvn spring-boot:run
```

## Testar

```shell
mvn test
```
