# Documentação técnica

Este documento registra somente o que é necessário para compreender, manter e
desenhar o sistema. O Swagger é a fonte dos formatos completos de request e
response; as migrations são a fonte definitiva do esquema físico.

## Visão do sistema

O frontend Angular consome a API REST. A API autentica com JWT, persiste dados no
PostgreSQL, guarda currículos no MinIO e envia mensagens por SMTP. No ambiente
Docker, o Mailpit captura os e-mails de desenvolvimento.

```text
Navegador -> Angular -> API Spring Boot -> PostgreSQL
                              |-> MinIO (arquivos de currículo)
                              `-> SMTP/Mailpit (e-mails)
```

## Perfis e responsabilidades

| Perfil | Operações principais |
| --- | --- |
| Candidato | consulta vagas publicadas; cria e completa uma candidatura; acompanha ou cancela a própria candidatura |
| Responsável por vaga | mantém vagas; consulta candidatos das vagas sob sua responsabilidade; altera etapas e registra feedback |
| Administrador | possui acesso de gestão; mantém templates e pode gerir vagas e candidaturas |

O cadastro público sempre cria um usuário com papel `CANDIDATO`. A API aplica a
autorização nos casos de uso; os guards do frontend servem apenas para navegação e
experiência de uso.

## Arquitetura da API

O código segue portas e adaptadores:

- `core`: entidades, enums, erros e objetos comuns; não depende das camadas externas;
- `app`: casos de uso, DTOs e contratos de entrada/saída;
- `infra`: JPA, segurança, MinIO, SMTP, eventos e configuração das implementações;
- `web`: controllers, requests, segurança HTTP e tratamento uniforme de erros.

Fluxo de uma chamada: `Controller -> porta de entrada -> caso de uso -> porta de
saída -> adapter`. A validação de formato ocorre em `web`; regras de negócio e
autorização pertencem a `core` e `app`.

## Fluxos de negócio

### Autenticação

1. O usuário se cadastra ou entra com e-mail e senha.
2. A API devolve access token e refresh token.
3. O access token autentica as chamadas; o refresh token é rotacionado na renovação.
4. Reutilização indevida ou logout revoga a família de refresh tokens.
5. Na recuperação de senha, a API persiste somente o hash do token e envia o link
   por e-mail; a confirmação consome o token e altera a senha.

### Vaga

1. Administrador ou responsável cria a vaga em `RASCUNHO`, informando ao menos um
   responsável.
2. Perguntas e requisitos podem ser adicionados diretamente ou copiados de um
   template.
3. A vaga segue `RASCUNHO -> PUBLICADA -> ENCERRADA` ou pode ser cancelada a partir
   de `RASCUNHO`/`PUBLICADA`.
4. Apenas vagas `PUBLICADA` e não excluídas aceitam candidatura.
5. Exclusão de vaga, pergunta, requisito e template é lógica por `excluido_em`.

### Candidatura

1. Um candidato inicia uma única candidatura por vaga; ela nasce `RASCUNHO`.
2. Ele responde às perguntas, classifica o atendimento aos requisitos e envia um
   currículo. Etapas inexistentes são consideradas concluídas.
3. Quando as três partes estão completas, a candidatura muda automaticamente para
   `ENVIADA` e gera notificações para candidato e responsáveis.
4. Responsáveis pela vaga ou administradores avaliam e alteram a etapa, com feedback
   opcional. Cada mudança cria um registro de histórico.
5. A coluna `versao` aplica optimistic locking para impedir que duas avaliações
   sobrescrevam uma à outra.
6. O candidato pode cancelar a própria candidatura enquanto ela não for terminal.

Estados:

```text
RASCUNHO -> ENVIADA -> TRIAGEM -> ENTREVISTA_COMPORTAMENTAL
                               -> ENTREVISTA_TECNICA
                               -> APROVADA | REJEITADA

RASCUNHO, ENVIADA, TRIAGEM e entrevistas -> CANCELADA
```

A implementação permite ao avaliador avançar ou reposicionar uma candidatura entre
estados não terminais e finais, exceto retornar a `RASCUNHO`; `CANCELADA` é terminal.

### Notificação

1. Eventos de candidatura são publicados após a operação de negócio.
2. Uma notificação é persistida por evento e destinatário antes da tentativa SMTP.
3. Sucesso marca `ENVIADA`; erro marca `FALHA` e guarda a mensagem e o número de
   tentativas.
4. O agendador reprocessa falhas até `NOTIFICATION_MAX_ATTEMPTS`. A chave única
   `(evento_id, usuario_id)` evita duplicação.

## Endpoints

Todos exigem JWT, exceto cadastro, login, refresh, recuperação de senha, Swagger e
health check.

| Grupo | Operações |
| --- | --- |
| `/auth` | cadastro, login, refresh, logout, solicitação e confirmação de recuperação |
| `/vagas` | listagem/consulta; criação simples ou completa; edição, status e exclusão; CRUD de perguntas e requisitos |
| `/templates-vaga` | CRUD de templates, perguntas e requisitos; criação de vaga a partir do template |
| `/vagas/{vagaId}/candidaturas` | criação pelo candidato e listagem filtrada para avaliação |
| `/candidaturas` | próprias candidaturas, resumo, detalhe, respostas, requisitos, status e cancelamento |
| `/candidaturas/{id}/curriculo` | envio/substituição e geração de URL temporária |

Paginação usa `page` baseado em zero e `pageSize`. Consulte
`/swagger-ui.html` para parâmetros, operadores de filtro, validações e schemas.

## Modelo de dados atual

### Identidade e sessão

| Tabela | Finalidade | Relações/restrições relevantes |
| --- | --- | --- |
| `usuarios` | identidade, perfil básico e credenciais | e-mail único; exclusão lógica; `data_admissao` obrigatória |
| `papeis` | catálogo dos três perfis | nome único e enumerado |
| `usuarios_papeis` | N:N entre usuários e papéis | PK composta |
| `refresh_tokens` | rotação e revogação de sessão | FK usuário; hash único; agrupamento por `familia_id` |
| `tokens_recuperacao_senha` | recuperação de senha | FK usuário; hash único; expiração e uso único |

### Vagas e templates

| Tabela | Finalidade | Relações/restrições relevantes |
| --- | --- | --- |
| `vagas` | dados e estado da vaga | status enumerado; exclusão lógica |
| `vagas_responsaveis` | N:N entre vagas e responsáveis | PK `(vaga_id, usuario_id)` |
| `perguntas_vaga` | formulário específico da vaga | N:1 vaga; tipo enumerado; ordem positiva; exclusão lógica |
| `requisitos_vaga` | requisitos avaliados pelo candidato | N:1 vaga; exclusão lógica |
| `templates_vaga` | modelo reutilizável | N:1 usuário responsável; exclusão lógica |
| `perguntas_template_vaga` | perguntas do template | N:1 template; tipo e ordem validados |
| `requisitos_template_vaga` | requisitos do template | N:1 template |

### Processo seletivo

| Tabela | Finalidade | Relações/restrições relevantes |
| --- | --- | --- |
| `candidaturas` | vínculo usuário-vaga e estado do processo | único `(usuario_id, vaga_id)`; flags de conclusão; `versao` otimista |
| `respostas_candidatura` | respostas às perguntas | único `(candidatura_id, pergunta_id)` |
| `respostas_requisitos_candidatura` | autoavaliação dos requisitos | único `(candidatura_id, requisito_id)`; nível enumerado |
| `curriculos` | metadados do arquivo no MinIO | um registro ativo por candidatura; limite de 5 MiB; cascade ao excluir candidatura |
| `historicos_candidatura` | auditoria de mudança de etapa | N:1 candidatura e N:1 autor; guarda status anterior, novo e feedback |
| `notificacoes` | fila e auditoria de e-mail | N:1 usuário; único por evento/destinatário; status e tentativas |

### Relações para o diagrama ER

```text
usuarios N--N papeis                  (usuarios_papeis)
usuarios N--N vagas                   (vagas_responsaveis)
usuarios 1--N templates_vaga
usuarios 1--N candidaturas
usuarios 1--N refresh_tokens
usuarios 1--N tokens_recuperacao_senha
usuarios 1--N notificacoes

vagas 1--N perguntas_vaga
vagas 1--N requisitos_vaga
vagas 1--N candidaturas

templates_vaga 1--N perguntas_template_vaga
templates_vaga 1--N requisitos_template_vaga

candidaturas 1--N respostas_candidatura N--1 perguntas_vaga
candidaturas 1--N respostas_requisitos_candidatura N--1 requisitos_vaga
candidaturas 1--N curriculos
candidaturas 1--N historicos_candidatura
usuarios 1--N historicos_candidatura              (autor)
```

## Decisões importantes do banco

- todas as chaves de domínio são UUID;
- datas operacionais usam `TIMESTAMPTZ`;
- Hibernate usa `ddl-auto=validate`; somente o Flyway altera o schema;
- exclusão lógica preserva vagas, templates, perguntas, requisitos, usuários e
  versões substituídas de currículo;
- o arquivo de currículo não fica no PostgreSQL: `storage_key`, checksum, tipo,
  nome e tamanho referenciam o objeto privado no MinIO;
- não existe mais a tabela `candidatos`: desde a migration V20, a candidatura se
  relaciona diretamente a `usuarios`;
- não existe migration V12. Isso é válido no Flyway e não deve ser “corrigido”
  renumerando migrations aplicadas.

## Configuração

As variáveis obrigatórias e o modo de execução estão no README. Variáveis opcionais
relevantes: `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, `MAIL_STARTTLS`,
`MAIL_FROM`, `PASSWORD_RESET_URL`, `NOTIFICATION_RETRY_DELAY_MS`,
`NOTIFICATION_MAX_ATTEMPTS`, `LOG_LEVEL_ROOT` e `LOG_LEVEL_APP`.

Em produção, use JWT longo e aleatório, credenciais externas ao repositório, bucket
privado, SMTP autenticado/TLS e HTTPS na borda.
