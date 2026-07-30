# Autenticação e autorização

## Objetivo

Garantir que apenas usuários autenticados e autorizados acessem as funcionalidades do sistema.

## Modelo de domínio

### Usuário

Representa a conta utilizada para autenticação e autorização.

O usuário deve armazenar:

- E-mail.
- Telefone.
- Hash da senha.
- Papel ou perfil de acesso.

A senha nunca deve ser armazenada em texto puro.

Um usuário pode possuir, no máximo, um perfil de candidato:

```text
Usuario 1 --- 0..1 Candidato
```

## Regras de negócio

- O e-mail deve identificar um único usuário.
- Apenas usuários autenticados podem acessar funcionalidades protegidas.
- A autorização deve considerar o papel do usuário.
- Funcionalidades administrativas devem ser restritas a administradores.
- Funcionalidades de candidatura devem ser restritas a usuários associados a um perfil de candidato.

## Critérios de aceitação

- Dado um usuário com credenciais válidas, quando ele se autenticar, então o acesso deve ser concedido.
- Dado um usuário com credenciais inválidas, quando ele tentar se autenticar, então o acesso deve ser negado.
- Dado um usuário sem o papel exigido, quando ele acessar uma funcionalidade restrita, então a operação deve ser rejeitada.
