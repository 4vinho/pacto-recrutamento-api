# Entidade: usuário

## Responsabilidade

Representar a conta usada para autenticação e autorização.

## Estrutura

- `usuarios`: id, e-mail normalizado e único, telefone opcional, hash da senha,
  indicador de conta ativa e auditoria.
- `papeis`: papéis únicos `ADMINISTRADOR`, `RESPONSAVEL_VAGA` e `CANDIDATO`.
- `usuarios_papeis`: associação N:N com chave composta.
- `refresh_tokens`: hash do token, usuário, família, expiração, uso, revogação e criação.

## Invariantes

- Senhas e tokens nunca são persistidos em texto puro.
- Um e-mail identifica no máximo um usuário.
- Um usuário pode possuir vários papéis e no máximo um perfil de candidato.
- Refresh token expirado, usado ou revogado não pode criar nova sessão.
