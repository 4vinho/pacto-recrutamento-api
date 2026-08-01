# Perfil do candidato

## Falta entregar

- Endpoint `GET /candidatos/me` ou equivalente para consultar o perfil do
  usuário autenticado.
- Operação explícita para atualizar os campos permitidos do perfil.
- Resumo do currículo atual independente de uma candidatura.

## Situação atual

O cadastro mantém e-mail e telefone em `Usuario`, mas não há
`CandidatoController` nem caso de uso de perfil. O modelo separado de candidato
foi removido pela migration `V20`. Currículos existem somente associados a uma
candidatura; portanto, o backend não sustenta uma tela geral de perfil/currículo.

Esse perfil não é citado expressamente no enunciado mínimo, mas é necessário
para completar o fluxo de tela planejado no frontend.
