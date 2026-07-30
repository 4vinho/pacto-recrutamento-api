# Autenticação JWT e refresh token

## Access token

O access token deve:

- Ser um JWT assinado.
- Expirar após 15 minutos.
- Possuir identificador próprio no claim `jti`.
- Identificar o usuário no claim `sub`.
- Informar os papéis autorizados.
- Possuir emissão e expiração.
- Não conter senha, telefone ou outros dados sensíveis.

Configuração proposta:

```yaml
security:
  jwt:
    access-token-expiration: 15m
    refresh-token-expiration: 7d
    issuer: pacto-recrutamento-api
```

Segredos e chaves devem vir de variáveis de ambiente.

## Papéis

- `ADMINISTRADOR`: administra usuários, vagas e templates.
- `RESPONSAVEL_VAGA`: acompanha candidaturas e altera seus status.
- `CANDIDATO`: mantém seu perfil, candidata-se e envia respostas.

Um usuário pode possuir mais de um papel.

## Refresh token

O refresh token deve:

- Ser um valor aleatório criptograficamente seguro.
- Ser enviado em texto puro apenas ao cliente.
- Ser persistido somente como hash.
- Possuir expiração configurável.
- Ser vinculado ao usuário e a uma família de tokens.
- Ser rotacionado a cada renovação.

Fluxo de renovação:

1. Cliente envia o refresh token.
2. API calcula seu hash e localiza o registro.
3. API verifica expiração, revogação e uso anterior.
4. API marca o token atual como utilizado.
5. API emite um novo access token.
6. API emite um novo refresh token da mesma família.
7. API persiste somente o hash do novo refresh token.

Se um token já utilizado for reapresentado, toda a família deve ser revogada por suspeita de reutilização.

## Endpoints planejados

```http
POST /auth/cadastro
POST /auth/login
POST /auth/refresh
POST /auth/logout
POST /auth/recuperacao-senha/solicitacoes
POST /auth/recuperacao-senha/confirmacoes
```

## Logout

O logout deve revogar o refresh token apresentado. O access token continuará válido somente até seu vencimento de, no máximo, 15 minutos.

## Recuperação de senha

Tokens de recuperação devem:

- Ser diferentes de access e refresh tokens.
- Possuir uso único.
- Ter duração curta e configurável.
- Ser persistidos somente como hash.
- Ser invalidados após a redefinição da senha.

Após redefinir a senha, a recomendação é revogar todos os refresh tokens do usuário.

## Dependências planejadas

- Biblioteca JWT compatível com Java 8.
- `spring-security-test` para testes de autorização.
- BCrypt fornecido pelo Spring Security.

A biblioteca e sua versão exata devem ser escolhidas no momento da implementação, verificando compatibilidade e manutenção.

## Testes obrigatórios

- Access token expira em 15 minutos.
- Login rejeita senha inválida.
- Refresh válido gera novo par de tokens.
- Refresh expirado ou revogado é rejeitado.
- Reutilização revoga a família.
- Usuário sem papel recebe acesso negado.
- Senhas e tokens puros não aparecem no banco nem nos logs.
