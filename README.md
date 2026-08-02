# Pacto Recrutamento — API

Backend de uma plataforma de recrutamento que cobre o processo completo: publicação de vagas, candidatura, avaliação, histórico e notificações. O projeto foi construído para demonstrar uma API de negócio além do CRUD, com regras de autorização, consistência transacional, concorrência e integrações reais.

> **Quer avaliar o projeto rapidamente?** Com Docker, a solução completa sobe com um comando e já inclui dados de demonstração para três perfis. Veja [Teste em 5 minutos](#teste-em-5-minutos).

## O que este projeto demonstra

- **Arquitetura de portas e adaptadores:** domínio e casos de uso isolados de HTTP, JPA, storage e SMTP.
- **Segurança de sessão:** JWT de curta duração, refresh token rotativo em cookie `HttpOnly`, proteção CSRF, revogação por família e recuperação de senha com token armazenado como hash.
- **Regras de negócio reais:** estados de vaga e candidatura, autorização por perfil e por responsável, exclusão lógica e candidatura única por vaga.
- **Consistência sob concorrência:** optimistic locking impede que avaliações simultâneas sobrescrevam dados silenciosamente.
- **Processamento resiliente:** notificações persistidas, disparadas por eventos e reprocessadas automaticamente em caso de falha.
- **Infraestrutura reproduzível:** PostgreSQL, MinIO, Mailpit, API e frontend orquestrados por Docker Compose.
- **Qualidade verificável:** 107 testes Java, testes de arquitetura e transações, JaCoCo, SonarQube e contrato OpenAPI executável.

## Stack

| Área | Tecnologias |
| --- | --- |
| Aplicação | Java 8, Spring Boot 2.7, Spring MVC, Validation |
| Persistência | Spring Data JPA, PostgreSQL, Flyway |
| Segurança | Spring Security, JWT, BCrypt, CSRF |
| Integrações | MinIO, SMTP/Mailpit |
| Qualidade | JUnit 5, Mockito, H2, JaCoCo, SonarQube |
| Entrega local | Docker, Docker Compose, health check e Swagger/OpenAPI |

## Teste em 5 minutos

### 1. Suba a solução completa

Pré-requisito: Docker com Docker Compose. Os repositórios da API e do frontend devem ficar lado a lado.

```powershell
git clone https://github.com/4vinho/pacto-recrutamento-api.git
git clone https://github.com/4vinho/pacto-recrutamento-web.git
cd pacto-recrutamento-api
Copy-Item .env.example .env
docker compose up --watch
```

No Linux ou macOS, substitua `Copy-Item` por `cp`. Aguarde os health checks e abra [http://localhost:4200](http://localhost:4200).

### 2. Explore os três pontos de vista

| Perfil | E-mail | Senha | O que avaliar |
| --- | --- | --- | --- |
| Administrador | `socrates@pacto.com` | `socrates` | templates, criação de vaga e visão de gestão |
| Responsável | `platao@pacto.com` | `platao` | candidatos, filtros, feedback e mudança de etapa |
| Candidato | `aristoteles@pacto.com` | `aristoteles` | vagas, candidatura em etapas e acompanhamento |

Sugestão de roteiro:

1. Entre como **candidato**, consulte uma vaga e acompanhe as candidaturas existentes.
2. Entre como **responsável**, abra uma vaga e altere a etapa de um candidato com feedback.
3. Confira o histórico da candidatura e o e-mail capturado no [Mailpit](http://localhost:8025).
4. Entre como **administrador** e crie uma vaga a partir de um template.
5. Explore e execute os endpoints pelo [Swagger UI](http://localhost:8080/swagger-ui.html).

As contas e a massa de demonstração são carregadas pelo Flyway. A migration é
idempotente e também pode completar um banco que já tenha sido inicializado.

> **Observação:** as candidaturas das vagas carregadas na massa de demonstração
> não possuem arquivos de currículo no MinIO. Para testar a visualização do
> currículo como administrador ou responsável, envie primeiro um PDF em uma
> candidatura usando uma conta de candidato.

## Arquitetura

```text
Navegador -> Angular -> controllers HTTP
                           |
                    portas de entrada
                           |
                     casos de uso
                           |
                    portas de saída
                 /         |          \
          PostgreSQL     MinIO       SMTP
```

O código preserva a direção das dependências:

- `core`: entidades, estados, erros e objetos independentes de framework;
- `app`: casos de uso, DTOs e contratos de entrada e saída;
- `infra`: adapters JPA, JWT, MinIO, SMTP, eventos e configurações;
- `web`: controllers, requests, segurança HTTP e tratamento uniforme de erros.

Os testes de arquitetura validam essas fronteiras automaticamente. O fluxo de uma chamada é `controller -> porta de entrada -> caso de uso -> porta de saída -> adapter`.

## Decisões técnicas que valem uma conversa

### Sessão segura

O access token autentica as requisições sem ser persistido no navegador. O refresh token é rotacionado a cada renovação e transportado em cookie protegido. A reutilização indevida revoga toda a família de tokens; logout e refresh também exigem token CSRF.

### Concorrência e auditoria

A candidatura possui uma versão para optimistic locking. Se dois avaliadores tentarem atualizar o mesmo registro, a API rejeita o estado obsoleto em vez de perder uma alteração. Toda mudança de etapa gera histórico com autor, estado anterior, novo estado e feedback.

### Notificações confiáveis

Eventos de candidatura desacoplam o fluxo principal do envio de e-mail. Cada notificação é persistida antes do SMTP; falhas registram motivo e quantidade de tentativas, e um agendador executa as retentativas. A chave de evento e destinatário evita duplicidade.

### Arquivos fora do banco

Currículos de até 5 MiB ficam em bucket privado no MinIO. O PostgreSQL armazena apenas metadados e checksum, e o acesso ocorre por URL temporária.

## Serviços locais

| Serviço | Endereço |
| --- | --- |
| Frontend | [localhost:4200](http://localhost:4200) |
| API | [localhost:8080](http://localhost:8080) |
| Swagger UI | [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| Health check | [localhost:8080/actuator/health](http://localhost:8080/actuator/health) |
| Mailpit | [localhost:8025](http://localhost:8025) |
| MinIO console | [localhost:9001](http://localhost:9001) |
| PostgreSQL | `localhost:5432` |

Para encerrar preservando os dados, use `docker compose down`. A opção `--volumes` também apaga os dados locais.

## Executar e validar somente a API

Para desenvolvimento local são necessários Java 8, Maven 3.8+, PostgreSQL, MinIO e SMTP. Com os serviços disponíveis nas portas padrão:

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Para executar a suíte e gerar o relatório de cobertura:

```powershell
mvn verify
```

O relatório fica em `target/site/jacoco/`. Veja também [SONARQUBE.md](SONARQUBE.md) para análise estática.

## Documentação técnica

- [DOCUMENTACAO.md](DOCUMENTACAO.md): regras, fluxos, estados, endpoints e modelo de dados;
- [Swagger UI](http://localhost:8080/swagger-ui.html): contrato executável, schemas e validações;
- `src/main/resources/db/migration`: evolução versionada do banco;
- `.env.example`: configuração completa do ambiente Docker, sem segredos reais.

## Escopo e próximos passos

O projeto prioriza a profundidade do fluxo de recrutamento e a qualidade do backend. Em uma evolução para produção, os próximos passos naturais seriam observabilidade distribuída, testes de integração com Testcontainers, paginação por cursor em consultas de alto volume e entrega contínua em ambiente de nuvem.
