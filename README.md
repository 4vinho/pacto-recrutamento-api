# Recrutamento API

Backend do sistema de recrutamento interno, desenvolvido com Java 8, Spring Boot,
PostgreSQL, MinIO e Flyway.

## Requisitos

Para execução com contêineres:

- Docker;
- Docker Compose.

Para execução local:

- Java 8;
- Maven 3.8+;
- PostgreSQL;
- MinIO;
- um servidor SMTP, como o Mailpit.

## Executar com Docker

Copie o arquivo de exemplo e, se necessário, altere as credenciais e a chave JWT:

```shell
cp .env.example .env
```

No PowerShell:

```powershell
Copy-Item .env.example .env
```

Suba todos os serviços:

```shell
docker compose up --build
```

Durante o desenvolvimento, o Compose Watch pode reconstruir a API quando houver
alterações em `src` ou no `pom.xml`:

```shell
docker compose watch
```

Serviços disponíveis:

| Serviço | Endereço |
| --- | --- |
| API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| PostgreSQL | `localhost:5432` |
| MinIO API | `http://localhost:9000` |
| MinIO Console | `http://localhost:9001` |
| Mailpit | `http://localhost:8025` |
| SMTP do Mailpit | `localhost:1025` |

Os dados do PostgreSQL e do MinIO são mantidos em volumes nomeados. O Mailpit é
destinado ao desenvolvimento local e mantém os e-mails apenas enquanto seu
contêiner estiver em execução.

## Notificações por e-mail

A API envia e-mails nos seguintes fluxos:

- criação de candidatura: notifica o candidato e os responsáveis pela vaga;
- alteração do status: notifica o candidato e informa o novo status;
- recuperação de senha: envia um link contendo o token de redefinição.

As notificações de candidatura são persistidas antes do envio. Falhas de SMTP não
desfazem a candidatura: o registro fica com status de falha e é reprocessado pelo
agendador. Eventos já enviados não são enviados novamente.

Para testar localmente:

1. Inicie a aplicação com `docker compose up --build`.
2. Crie uma candidatura, altere seu status ou solicite recuperação de senha.
3. Abra `http://localhost:8025` para visualizar a mensagem capturada pelo Mailpit.

### Configuração de e-mail

| Variável | Padrão | Finalidade |
| --- | --- | --- |
| `MAIL_HOST` | `localhost` | Host do servidor SMTP. No Compose, a API usa `mailpit`. |
| `MAIL_PORT` | `1025` | Porta SMTP. |
| `MAIL_USERNAME` | vazio | Usuário SMTP, quando exigido pelo provedor. |
| `MAIL_PASSWORD` | vazio | Senha SMTP, quando exigida pelo provedor. |
| `MAIL_SMTP_AUTH` | `false` | Habilita autenticação SMTP. |
| `MAIL_STARTTLS` | `false` | Habilita STARTTLS. |
| `MAIL_FROM` | `nao-responda@pacto.local` | Remetente das mensagens. |
| `PASSWORD_RESET_URL` | `http://localhost:3000/redefinir-senha` | Página do frontend usada no link de recuperação. |
| `NOTIFICATION_RETRY_DELAY_MS` | `60000` | Intervalo entre ciclos de retentativa. |
| `NOTIFICATION_MAX_ATTEMPTS` | `5` | Máximo de tentativas por notificação. |

Em produção, configure um provedor SMTP real e habilite autenticação e TLS conforme
as exigências desse provedor.

## Contas iniciais

Na primeira inicialização do banco, o Flyway cria uma conta para cada perfil:

| Perfil | E-mail | Senha |
| --- | --- | --- |
| Administrador | `socrates@pacto.com` | `socrates` |
| Responsável por vaga | `platao@pacto.com` | `platao` |
| Candidato | `aristoteles@pacto.com` | `aristoteles` |

## Executar sem Docker

Defina as variáveis obrigatórias de banco de dados, MinIO, servidor e segurança:

- `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD`;
- `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY` e `MINIO_BUCKET`;
- `SERVER_PORT` e `JWT_SECRET`;
- `MAIL_HOST` e `MAIL_PORT`.

As demais configurações de e-mail possuem valores padrão para desenvolvimento.
Depois, execute:

```shell
mvn spring-boot:run
```

## Testar

```shell
mvn test
```
