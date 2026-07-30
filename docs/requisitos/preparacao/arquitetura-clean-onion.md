# Arquitetura Clean/Onion

## Objetivo

Separar regras de negócio, casos de uso, infraestrutura e transporte HTTP, mantendo as dependências voltadas para o núcleo.

Os nomes dos pacotes Java devem ser minúsculos:

```text
br.com.pacto.recrutamento
├── core
├── app
├── infra
└── web
```

## Regra de dependência

```text
Web ──> App ──> Core
Infra ─────────> Core
Infra ─────────> App
```

- `core` não depende de Spring, JPA ou HTTP.
- `app` depende do `core` e declara portas.
- `infra` implementa portas de persistência, segurança e integrações.
- `web` expõe controllers e converte contratos HTTP em comandos da aplicação.

## Core

Contém:

- Entidades de domínio.
- Value objects.
- Enums de domínio.
- Regras e exceções de negócio.
- Interfaces estritamente pertencentes ao domínio, quando necessárias.

Organização por entidade:

```text
core
├── usuario
├── candidato
├── vaga
├── candidatura
├── template
└── notificacao
```

Exemplo:

```text
core/candidatura/
├── Candidatura.java
├── CandidaturaId.java
├── StatusCandidatura.java
├── RespostaCandidatura.java
└── CandidaturaException.java
```

## App

Contém:

- Casos de uso.
- Comandos de entrada.
- Resultados da aplicação.
- Portas de persistência e serviços externos.
- Controle de transação por caso de uso.

Exemplo:

```text
app/candidatura/
├── CriarCandidatura.java
├── CriarCandidaturaCommand.java
├── ResponderCandidatura.java
├── ResponderCandidaturaCommand.java
├── CandidaturaRepository.java
└── NotificacaoPort.java
```

O armazenamento de currículos deve ser representado por uma porta em `app/candidato`:

```text
ArquivoStorage
├── salvar
├── excluir
└── gerarUrlTemporaria
```

Os casos de uso não devem depender diretamente do SDK do MinIO.

## Infra

Contém:

- Entidades JPA.
- Repositórios Spring Data.
- Adaptadores das portas.
- Flyway e configuração de banco.
- Implementação JWT.
- BCrypt.
- Envio de notificações.

Exemplo:

```text
infra/candidatura/
├── CandidaturaJpaEntity.java
├── CandidaturaJpaRepository.java
├── CandidaturaRepositoryAdapter.java
└── CandidaturaMapper.java
```

O adaptador do armazenamento deve ficar em:

```text
infra/arquivo/
├── MinioArquivoStorage.java
└── MinioProperties.java
```

As entidades JPA devem permanecer separadas das entidades de domínio para impedir que anotações e comportamento do ORM contaminem o núcleo.

## Web

Contém:

- Controllers REST.
- DTOs de requisição e resposta.
- Validação de entrada.
- Conversores entre DTOs e comandos.
- Tratamento global de exceções.
- Configuração HTTP de segurança.

Exemplo:

```text
web/candidatura/
├── CandidaturaController.java
├── CriarCandidaturaRequest.java
├── ResponderCandidaturaRequest.java
└── CandidaturaResponse.java
```

## Compartilhamento

Código compartilhado deve ser mínimo:

```text
core/shared
app/shared
infra/shared
web/shared
```

Não deve existir uma pasta genérica usada como depósito de classes sem responsabilidade clara.

## Injeção de dependências

Configurações Spring devem montar os casos de uso e adaptadores fora do `core`.

Os casos de uso devem depender de interfaces:

```text
CriarCandidatura
    -> CandidaturaRepository
    -> VagaRepository
    -> CandidatoRepository
    -> PublicadorEvento
```

## Transações

- A fronteira transacional deve ser o caso de uso.
- O lote de uma ou mais respostas deve ser atômico.
- Controllers não devem iniciar transações.
- Entidades de domínio não devem acessar repositórios.

## Verificação arquitetural

Planejar testes ArchUnit para garantir:

- `core` não depende de Spring.
- `core` não depende de `infra` ou `web`.
- Controllers existem somente em `web`.
- Adaptadores JPA existem somente em `infra`.
