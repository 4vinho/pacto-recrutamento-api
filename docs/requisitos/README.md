# Requisitos do backend

Esta é a entrada principal dos requisitos do sistema de recrutamento. O mesmo
recurso aparece em visões diferentes para separar **o que ele é**, **o que ele
faz**, **como é persistido/integrado** e **como é exposto por HTTP**.

## Como navegar

| Visão                             | Pergunta respondida                                                     |
|-----------------------------------|-------------------------------------------------------------------------|
| [Entidades](entidades/README.md)  | Quais dados, relacionamentos, estados e invariantes existem?            |
| [Services](services/README.md)    | Quais casos de uso e fluxos de negócio devem ser executados?            |
| [Infraestrutura](infra/README.md) | Como banco, segurança, storage e notificações suportam os casos de uso? |
| [Endpoints](endpoints/README.md)  | Quais contratos HTTP a API oferece?                                     |

Dentro de cada visão, os requisitos estão agrupados pelo agregado principal ou
tabela. Perguntas e requisitos pertencem a `vaga`; respostas pertencem a
`candidatura`; papéis, tokens e recuperação de senha pertencem a `usuario`.

## Mapa dos agregados

| Agregado         | Tabelas principais                                                      | Prioridade                          |
|------------------|-------------------------------------------------------------------------|-------------------------------------|
| Usuário          | `usuarios`, `papeis`, `usuarios_papeis`, `refresh_tokens`               | Obrigatório                         |
| Candidato        | `candidatos`                                                            | Obrigatório                         |
| Currículo        | `curriculos`                                                            | Complementar                        |
| Vaga             | `vagas`, `perguntas_vaga`, `requisitos_vaga`                            | Obrigatório                         |
| Candidatura      | `candidaturas`, `respostas_candidatura`                                 | Obrigatório                         |
| Template de vaga | `templates_vaga`, `perguntas_template_vaga`, `requisitos_template_vaga` | Evolução                            |
| Notificação      | `notificacoes`                                                          | Obrigatório no fluxo de candidatura |

## Regras globais

- Java 8+, Spring Boot 2.7.18, Angular e PostgreSQL.
- Identificadores UUID; datas em UTC e enums persistidos como texto.
- Exclusão lógica quando o histórico precisar ser preservado.
- Dependências seguem `web -> app -> core`; `infra` implementa portas de `app`.
- `core` concentra as entidades do domínio e seus mapeamentos JPA, sem depender
  de `infra`, HTTP, MinIO ou bibliotecas JWT.
- Services dependem de interfaces em `app/ports`; implementações que acessam
  banco, storage ou outros sistemas externos ficam em `infra`.
- Toda entrada externa deve ser validada e erros devem usar o contrato HTTP comum.
- A implementação deve começar por testes e entregar somente fluxos funcionais.

## Estado dos requisitos

- **Confirmado:** decorre diretamente do desafio ou de uma decisão já adotada.
- **Proposto:** detalhamento técnico que deve ser validado durante a implementação.
- **Pendente:** decisão de produto ainda não definida.

Os documentos anteriores foram preservados em
[fontes-originais](fontes-originais/README.md) para rastreabilidade.
