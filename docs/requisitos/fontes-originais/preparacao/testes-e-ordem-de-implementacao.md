# Testes e ordem de implementação

## Dependências planejadas

Adicionar quando a implementação começar:

- Flyway.
- Biblioteca JWT compatível com Java 8.
- `spring-security-test`.
- Testcontainers PostgreSQL.
- ArchUnit.
- SDK S3-compatible para integração com MinIO.

Manter:

- Spring Boot Starter Test.
- H2 apenas para testes que não dependam do comportamento do PostgreSQL.

## Estratégia de testes

### Testes unitários

Cobrir:

- Entidades e value objects.
- Regras de transição de status.
- Casos de uso com portas simuladas.
- Validações de lotes de respostas.
- Rotação e revogação de refresh tokens.

### Testes de integração

Cobrir:

- Adaptadores JPA.
- Migrations Flyway.
- Restrições do PostgreSQL.
- Segurança e filtros JWT.
- Transações.

### Testes web

Cobrir:

- Contratos JSON.
- Validação dos DTOs.
- Status HTTP.
- `ApiResponse<T>`.
- Autorização por papel.

### Testes arquiteturais

Cobrir as dependências permitidas entre `core`, `app`, `infra` e `web`.

## Ordem de implementação

### Etapa 1 — Fundação

1. Adicionar dependências.
2. Criar os pacotes da arquitetura.
3. Criar `ApiResponse<T>`.
4. Criar o tratamento global de exceções.
5. Configurar relógio UTC.
6. Configurar Flyway e PostgreSQL para testes.

### Etapa 2 — Usuário e segurança

1. Criar modelo de usuário e papéis.
2. Criar migrations.
3. Implementar `CadastrarUsuario`.
4. Implementar BCrypt.
5. Implementar login.
6. Implementar access token de 15 minutos.
7. Implementar refresh token rotativo.
8. Implementar logout.
9. Implementar recuperação de senha.

### Etapa 3 — Candidato

1. Criar entidade e migration.
2. Implementar `CriarCandidato`.
3. Implementar `AtualizarCandidato`.
4. Configurar MinIO para desenvolvimento.
5. Criar a porta `ArquivoStorage`.
6. Implementar o adaptador MinIO.
7. Implementar upload, substituição e exclusão lógica do currículo.
8. Implementar geração de URL temporária.

### Etapa 4 — Vagas

1. Criar vaga, perguntas e requisitos.
2. Implementar comandos de criação.
3. Implementar atualização.
4. Implementar mudança de status.
5. Implementar exclusão lógica.

### Etapa 5 — Candidaturas

1. Criar candidatura e respostas.
2. Implementar `CriarCandidatura`.
3. Implementar o envio atômico de uma ou mais respostas.
4. Implementar atualização de status.
5. Implementar cancelamento.

### Etapa 6 — Notificações

1. Publicar eventos do domínio.
2. Persistir notificações.
3. Implementar nova tentativa em caso de falha.

### Etapa 7 — Templates

1. Criar entidades e migrations.
2. Implementar comandos de manutenção.
3. Implementar cópia do template para a vaga.

### Etapa 8 — Consultas

Definir os endpoints de consulta de acordo com as necessidades das telas do frontend.

## Processo por caso de uso

Cada caso de uso deve seguir:

1. Escrever critérios de aceitação.
2. Criar um teste que falha.
3. Implementar o domínio mínimo.
4. Implementar o caso de uso.
5. Implementar a porta e o adaptador.
6. Expor o endpoint.
7. Executar testes unitários, de integração e web.
8. Refatorar preservando os testes.
