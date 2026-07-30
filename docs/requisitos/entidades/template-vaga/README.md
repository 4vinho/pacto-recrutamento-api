# Entidade: template de vaga

## Responsabilidade

Reutilizar a estrutura de vagas sem vincular o histórico ao template.

## Estrutura

Tabelas `templates_vaga`, `perguntas_template_vaga` e
`requisitos_template_vaga`, com campos equivalentes aos de vaga.

## Invariantes

- Criar vaga a partir de template copia seus dados.
- Alterar ou excluir o template não modifica vagas já criadas.
- Perguntas e requisitos pertencem ao template informado.
- Exclusão é lógica.

Este agregado é evolução posterior ao fluxo principal.
