# Guia de implementação

## Objetivo

Servir como roadmap executável para a construção do backend, indicando a ordem das entregas, os resultados esperados e os critérios para avançar.

Este documento não implementa nenhuma etapa. Cada item deve ser realizado e validado antes de seguir para o próximo.

## Princípios de execução

- Implementar uma etapa pequena por vez.
- Começar cada comportamento com um teste que falha.
- Manter `core` independente de Spring, JPA, HTTP e MinIO.
- Entregar cada funcionalidade verticalmente: domínio, caso de uso, infraestrutura, endpoint e testes.
- Não criar endpoints de consulta antes de identificar as necessidades das telas.
- Não avançar com testes quebrados.
- Registrar novas decisões nos requisitos antes de alterar o desenho estabelecido.

---

## Etapa 1 — Ambiente Docker

### Objetivo

Subir PostgreSQL, MinIO e a API com um único comando.

### 1.1 Criar o Dockerfile da API

Implementar um Dockerfile multi-stage:

1. Imagem Maven compila e empacota a aplicação.
2. Imagem Java executa somente o artefato produzido.
3. Processo executado por usuário sem privilégios administrativos.
4. Porta `8080` exposta.
5. Configuração recebida por variáveis de ambiente.

### 1.2 Criar o Docker Compose

Serviços obrigatórios:

```text
postgres
minio
minio-init
api
```

Responsabilidades:

- `postgres`: banco relacional da aplicação.
- `minio`: armazenamento privado dos currículos.
- `minio-init`: cria o bucket `curriculos` sem acesso público.
- `api`: executa o Spring Boot e se conecta aos outros serviços pela rede do Compose.

### 1.3 Configurar PostgreSQL

Definir:

- Banco `recrutamento`.
- Usuário próprio da aplicação.
- Senha por variável de ambiente.
- Volume nomeado para persistência.
- Health check com `pg_isready`.

Não utilizar credenciais reais no repositório.

### 1.4 Configurar MinIO

Definir:

- Credenciais por variável de ambiente.
- Porta da API S3-compatible.
- Porta do console administrativo.
- Volume nomeado para persistência.
- Health check.
- Bucket privado `curriculos`.

O serviço `minio-init` deve aguardar o MinIO ficar saudável e criar o bucket de maneira idempotente.

### 1.5 Configurar a API

Variáveis inicialmente necessárias:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
MINIO_ENDPOINT
MINIO_ACCESS_KEY
MINIO_SECRET_KEY
MINIO_BUCKET
JWT_SECRET
SERVER_PORT
```

Criar um `.env.example` somente com valores de desenvolvimento ou placeholders. O `.env` real não deve ser versionado.

### 1.6 Definir inicialização e dependências

- A API deve aguardar PostgreSQL e MinIO ficarem saudáveis.
- O banco deve manter os dados após reinício.
- O MinIO deve manter os arquivos após reinício.
- A remoção comum dos containers não deve apagar os volumes.

### Critério de conclusão

```shell
docker compose up --build
```

deve:

- Subir todos os serviços.
- Criar o banco.
- Criar o bucket privado.
- Inicializar a API.
- Preservar banco e arquivos após reiniciar os containers.

---

## Etapa 2 — Dependências e configuração-base

### Objetivo

Preparar o projeto para migrations, segurança, storage e testes.

### Dependências planejadas

- Flyway.
- Biblioteca JWT compatível com Java 8.
- SDK S3-compatible.
- `spring-security-test`.
- Testcontainers PostgreSQL.
- Testcontainers ou container de apoio para MinIO.
- ArchUnit.

### Configurações

- Manter `spring.jpa.hibernate.ddl-auto=validate`.
- Desabilitar Open Session in View.
- Configurar serialização de datas em UTC.
- Separar configurações de desenvolvimento, teste e produção.
- Carregar segredos somente por configuração externa.

### Critério de conclusão

- Aplicação compila.
- Testes existentes passam.
- Nenhum segredo foi adicionado ao Git.
- Contexto Spring inicia com os containers disponíveis.

---

## Etapa 3 — Estrutura Clean/Onion

### Objetivo

Criar os limites arquiteturais antes das funcionalidades.

Estrutura:

```text
br.com.pacto.recrutamento
├── core
├── app
├── infra
└── web
```

Dentro de cada camada, organizar por entidade ou funcionalidade:

```text
core/usuario
core/candidato
core/vaga
core/candidatura

app/usuario
app/candidato
app/vaga
app/candidatura

infra/usuario
infra/candidato
infra/vaga
infra/candidatura

web/usuario
web/candidato
web/vaga
web/candidatura
```

Criar testes ArchUnit para proteger as dependências:

- `core` não depende das outras camadas.
- `app` depende somente de `core`.
- `infra` implementa portas de `app`.
- `web` utiliza casos de uso de `app`.

### Critério de conclusão

- Pacotes-base criados.
- Primeiro teste arquitetural passando.
- Nenhuma anotação Spring ou JPA presente em `core`.

---

## Etapa 4 — Contrato HTTP compartilhado

### Objetivo

Padronizar respostas, validações e erros antes dos endpoints funcionais.

Implementar:

- `ApiResponse<T>` com `statusCode`, `message` e `data`.
- DTO para erros de campo.
- Tratamento global de exceções.
- Exceções para não encontrado, conflito e regra de negócio.
- Conversão consistente para HTTP 400, 401, 403, 404, 409 e 500.
- Validação Bean Validation nos DTOs de entrada.

### Testes

- Resposta de sucesso possui o formato padrão.
- Erro de validação lista os campos inválidos.
- Exceção inesperada não expõe stack trace.
- `statusCode` no corpo corresponde ao status HTTP.

### Critério de conclusão

- Contrato testado com endpoint técnico provisório ou teste isolado da camada web.
- Nenhuma entidade JPA é serializada diretamente.

---

## Etapa 5 — Flyway e modelo inicial

### Objetivo

Criar o banco de forma versionada.

Ordem planejada:

```text
V1__criar_usuarios_e_papeis.sql
V2__criar_refresh_tokens.sql
V3__criar_candidatos.sql
V4__criar_curriculos.sql
V5__criar_vagas.sql
V6__criar_perguntas_e_requisitos.sql
V7__criar_candidaturas_e_respostas.sql
V8__criar_notificacoes.sql
V9__criar_templates.sql
```

Durante a implementação, criar somente a migration exigida pela fatia atual. Não é necessário escrever todas antecipadamente.

### Testes

- Executar migrations em PostgreSQL vazio.
- Inicializar Hibernate com `ddl-auto=validate`.
- Validar restrições únicas e chaves estrangeiras.
- Executar novamente sem alterações ou falhas.

### Critério de conclusão

- `V1` e `V2` criadas para iniciar autenticação.
- Papéis iniciais inseridos.
- Validação Hibernate funcionando.

---

## Etapa 6 — Cadastro de usuário

### Objetivo

Entregar a primeira fatia funcional completa.

Implementar:

1. Entidade e value objects de usuário.
2. Validação e normalização de e-mail.
3. Porta de persistência.
4. Caso de uso `CadastrarUsuario`.
5. Adaptador JPA.
6. Hash BCrypt.
7. DTOs.
8. Endpoint `POST /auth/cadastro`.
9. Resposta padrão.

### Regras principais

- E-mail único.
- Senha nunca armazenada em texto puro.
- Papel inicial definido conforme a regra de cadastro.
- Dados sensíveis não retornados.

### Critério de conclusão

- Testes unitários, integração e web passando.
- Usuário persistido no PostgreSQL com senha em hash.

---

## Etapa 7 — Login, JWT e refresh token

### Objetivo

Concluir o fluxo básico de autenticação.

Implementar em ordem:

1. Autenticação de credenciais.
2. Access token JWT de 15 minutos.
3. Inclusão dos papéis no token.
4. Refresh token aleatório persistido somente como hash.
5. Rotação do refresh token.
6. Detecção de reutilização.
7. Revogação da família de tokens.
8. Logout.
9. Filtro de autenticação JWT.
10. Autorização por papel.

Endpoints:

```http
POST /auth/login
POST /auth/refresh
POST /auth/logout
```

### Critério de conclusão

- Login gera access e refresh token.
- Access token expira em 15 minutos.
- Refresh válido gera um novo par.
- Refresh reutilizado revoga sua família.
- Endpoints protegidos rejeitam acesso sem autenticação ou papel.

---

## Etapa 8 — Recuperação de senha

Implementar:

```http
POST /auth/recuperacao-senha/solicitacoes
POST /auth/recuperacao-senha/confirmacoes
```

Requisitos:

- Token de uso único.
- Duração curta.
- Persistência somente em hash.
- Resposta que não revele se o e-mail existe.
- Revogação das sessões após redefinir a senha.

### Critério de conclusão

- Fluxo completo testado.
- Token expirado, inválido ou reutilizado é rejeitado.

---

## Etapa 9 — Perfil do candidato

Implementar:

```http
POST /candidatos
PUT  /candidatos/me
```

Entregas:

- Entidade `Candidato`.
- Migration `V3`.
- Relação única com usuário.
- Casos de uso de criação e atualização.
- Autorização para modificar somente o próprio perfil.

### Critério de conclusão

- Um usuário não consegue criar dois perfis.
- Um candidato não consegue alterar o perfil de outro.

---

## Etapa 10 — Currículo e MinIO

Implementar:

1. Migration `V4`.
2. Entidade de metadados `Curriculo`.
3. Porta `ArquivoStorage`.
4. Adaptador `MinioArquivoStorage`.
5. Validação de PDF e limite de 5 MB.
6. Checksum SHA-256.
7. Upload.
8. Substituição do currículo.
9. Compensação quando banco ou storage falhar.
10. Geração de URL temporária.

Endpoint inicial:

```http
POST /candidatos/me/curriculo
```

O endpoint de obtenção da URL será definido quando a tela precisar do download.

### Critério de conclusão

- PDF válido é armazenado.
- Metadados são persistidos.
- Apenas um currículo permanece ativo.
- Arquivos inválidos são rejeitados.
- Reiniciar containers não perde o arquivo.

---

## Etapa 11 — Vagas

Implementar nesta ordem:

1. Entidade `Vaga`.
2. Responsável pela vaga.
3. Status da vaga.
4. Migration `V5`.
5. `CriarVaga`.
6. `AtualizarVaga`.
7. `AlterarStatusVaga`.
8. `ExcluirVaga`.

Endpoints:

```http
POST   /vagas
PUT    /vagas/{vagaId}
PATCH  /vagas/{vagaId}/status
DELETE /vagas/{vagaId}
```

### Critério de conclusão

- Apenas papel autorizado mantém vagas.
- Transições inválidas de status são rejeitadas.
- Exclusão é lógica.

---

## Etapa 12 — Perguntas e requisitos

Implementar migration `V6` e os casos de uso:

```text
CriarPerguntaDaVaga
AtualizarPerguntaDaVaga
ExcluirPerguntaDaVaga
CriarRequisitoDaVaga
AtualizarRequisitoDaVaga
ExcluirRequisitoDaVaga
```

Garantir:

- Relação com a vaga informada.
- Ordem positiva das perguntas.
- Tipo de resposta válido.
- Exclusão lógica.

### Critério de conclusão

- Perguntas e requisitos não podem ser manipulados através de outra vaga.
- Regras de validação cobertas por testes.

---

## Etapa 13 — Candidaturas

Implementar:

1. Migration `V7`.
2. Entidade `Candidatura`.
3. Restrição única entre candidato e vaga.
4. `CriarCandidatura`.
5. `AtualizarStatusCandidatura`.
6. `CancelarCandidatura`.

### Regras principais

- Vaga deve aceitar candidaturas.
- Candidato não pode se candidatar duas vezes.
- Status inicial `ENVIADA`.
- Somente responsável autorizado altera o processo.
- Somente o candidato cancela a própria candidatura.

### Critério de conclusão

- Regras de autorização e transição cobertas.
- Concorrência não permite candidatura duplicada.

---

## Etapa 14 — Respostas da candidatura

Implementar:

```http
POST /candidaturas/{candidaturaId}/respostas
```

O endpoint deve receber uma ou mais respostas.

Garantir:

- Coleção não vazia.
- Perguntas sem duplicidade no lote.
- Perguntas pertencentes à vaga.
- Valores compatíveis com o tipo da pergunta.
- Processamento em uma única transação.
- Nenhuma resposta persistida quando qualquer item for inválido.

### Critério de conclusão

- Lotes com um ou vários itens funcionam.
- Falha parcial provoca rollback completo.
- Uma candidatura não possui duas respostas para a mesma pergunta.

---

## Etapa 15 — Notificações

Implementar:

1. Migration `V8`.
2. Eventos `CandidaturaCriada` e `StatusCandidaturaAlterado`.
3. Persistência de notificações.
4. Notificação do candidato.
5. Notificação do responsável.
6. Registro e nova tentativa após falhas.

O envio de e-mail pode ser simulado inicialmente, mantendo a integração atrás de uma porta.

### Critério de conclusão

- Eventos de negócio disparam notificações.
- Falha de envio não perde a ocorrência.

---

## Etapa 16 — Templates de vaga

Executar após o fluxo principal estar funcional.

Implementar:

- Migration `V9`.
- Template de vaga.
- Perguntas do template.
- Requisitos do template.
- Comandos de criação, atualização e exclusão.
- Cópia do template para uma nova vaga.

### Critério de conclusão

- Alterar o template não modifica vagas já criadas.
- Exclusões são lógicas.

---

## Etapa 17 — Consultas orientadas pelo frontend

Somente após desenhar as telas:

1. Listar os dados necessários por tela.
2. Definir filtros, ordenação e paginação.
3. Criar DTOs de leitura específicos.
4. Implementar consultas sem expor entidades JPA.
5. Evitar carregamentos N+1.

### Critério de conclusão

- Cada consulta existe para atender uma necessidade real do frontend.
- Contratos estão documentados e testados.

---

## Etapa 18 — Qualidade e entrega

### Backend

- Executar todos os testes.
- Revisar cobertura dos fluxos críticos.
- Validar dependências arquiteturais.
- Validar logs sem informações sensíveis.
- Testar migrations do zero.
- Testar reinicialização dos containers.
- Documentar variáveis de ambiente.

### Docker

- Fixar versões das imagens.
- Validar health checks.
- Validar volumes.
- Verificar que segredos não estão nas imagens.
- Testar `docker compose up --build` em ambiente limpo.

### Documentação

- Atualizar README.
- Documentar execução e testes.
- Documentar usuários ou dados iniciais de demonstração.
- Documentar decisões e limitações conhecidas.

### Critério final

Uma pessoa sem conhecimento prévio do projeto deve conseguir:

1. Clonar o repositório.
2. Configurar o `.env`.
3. Subir a solução com Docker Compose.
4. Executar os testes.
5. Utilizar os fluxos principais documentados.

---

## Checklist de acompanhamento

- [ ] Etapa 1 — Ambiente Docker
- [ ] Etapa 2 — Dependências e configuração-base
- [ ] Etapa 3 — Estrutura Clean/Onion
- [ ] Etapa 4 — Contrato HTTP compartilhado
- [ ] Etapa 5 — Flyway e modelo inicial
- [ ] Etapa 6 — Cadastro de usuário
- [ ] Etapa 7 — Login, JWT e refresh token
- [ ] Etapa 8 — Recuperação de senha
- [ ] Etapa 9 — Perfil do candidato
- [ ] Etapa 10 — Currículo e MinIO
- [ ] Etapa 11 — Vagas
- [ ] Etapa 12 — Perguntas e requisitos
- [ ] Etapa 13 — Candidaturas
- [ ] Etapa 14 — Respostas da candidatura
- [ ] Etapa 15 — Notificações
- [ ] Etapa 16 — Templates de vaga
- [ ] Etapa 17 — Consultas orientadas pelo frontend
- [ ] Etapa 18 — Qualidade e entrega
