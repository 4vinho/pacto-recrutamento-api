# Vagas e templates

## Vagas entregues

- Cadastro simples e cadastro completo com perguntas e requisitos.
- Consulta detalhada e listagem paginada com busca, status e ordenação.
- Atualização dos dados gerais, alteração de status e exclusão lógica.
- Inclusão, alteração e remoção de perguntas e requisitos.
- Múltiplos responsáveis por vaga e validação de autorização do responsável.
- Estados de vaga `RASCUNHO`, `PUBLICADA`, `ENCERRADA` e `CANCELADA`.

O contrato HTTP está em `/vagas`, incluindo `POST /vagas/completa`, operações
em `/{vagaId}`, `/{vagaId}/status`, `/{vagaId}/perguntas` e
`/{vagaId}/requisitos`.

## Templates entregues

- Listagem paginada, busca e consulta detalhada.
- Criação, edição e exclusão lógica de templates.
- Gestão de perguntas e requisitos do template.
- Criação de vaga independente a partir de um template.

O contrato HTTP está em `/templates-vaga`, incluindo a criação de vaga por
`POST /templates-vaga/{templateId}/vagas`.

## Evidências principais

`VagaController`, `TemplateVagaController`, respectivos services, ports,
adapters JPA, repositórios, testes e migrations `V5`, `V6`, `V9` e `V17`.
