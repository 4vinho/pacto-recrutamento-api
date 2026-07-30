# Flyway e migrations

## Objetivo

Versionar a estrutura do PostgreSQL e permitir que qualquer ambiente seja criado de maneira reprodutível.

## Dependência planejada

Adicionar `flyway-core` ao `pom.xml`, utilizando a versão compatível gerenciada pelo Spring Boot 2.7.18.

## Configuração planejada

Manter:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

O Hibernate validará o mapeamento, mas não criará nem alterará tabelas.

O Flyway deverá:

- Executar na inicialização.
- Localizar scripts em `classpath:db/migration`.
- Interromper a aplicação se uma migration falhar.
- Não utilizar `baseline-on-migrate` sem necessidade comprovada.

## Estrutura

```text
src/main/resources/db/migration/
├── V1__criar_usuarios_e_papeis.sql
├── V2__criar_refresh_tokens.sql
├── V3__criar_candidatos.sql
├── V4__criar_curriculos.sql
├── V5__criar_vagas.sql
├── V6__criar_perguntas_e_requisitos.sql
├── V7__criar_candidaturas_e_respostas.sql
├── V8__criar_notificacoes.sql
└── V9__criar_templates.sql
```

## Regras

- Uma migration aplicada nunca deve ser editada.
- Correções devem ser feitas em uma nova migration.
- Cada migration deve possuir uma responsabilidade clara.
- Chaves estrangeiras, índices e restrições devem ter nomes explícitos.
- Valores iniciais dos papéis devem ser inseridos por migration.
- Migrations devem ser testadas contra PostgreSQL.
- H2 não deve ser usado para validar comportamento específico do PostgreSQL.

## Testes planejados

- Inicializar um PostgreSQL limpo.
- Executar todas as migrations.
- Inicializar o contexto Spring com `ddl-auto: validate`.
- Verificar restrições únicas e chaves estrangeiras relevantes.
- Verificar que uma segunda execução não produz mudanças.
