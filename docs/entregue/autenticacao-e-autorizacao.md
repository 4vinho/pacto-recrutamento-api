# Autenticação e autorização

## Entregue

- Cadastro de usuário candidato com e-mail, telefone e senha.
- Login com senha codificada por BCrypt e emissão de access token JWT e refresh
  token.
- Renovação de sessão com rotação/revogação de refresh token e logout.
- Recuperação e redefinição de senha por token; o canal de envio está simulado.
- Filtro JWT stateless e respostas JSON padronizadas para `401` e `403`.
- Perfis `ADMINISTRADOR`, `RESPONSAVEL_VAGA` e `CANDIDATO`, com autorização de
  negócio adicional para recursos vinculados a uma vaga.
- Rotas públicas limitadas a autenticação, documentação e healthcheck; demais
  endpoints exigem autenticação.

## Endpoints

- `POST /auth/cadastro`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `POST /auth/recuperacao-senha/solicitacoes`
- `POST /auth/recuperacao-senha/confirmacoes`

## Evidências principais

`AuthController`, `UsuarioService`, `JwtAuthenticationFilter`,
`SecurityConfiguration`, adapters de usuário/refresh/recuperação e migrations
`V1`, `V2`, `V11`, `V14` e `V18`.
