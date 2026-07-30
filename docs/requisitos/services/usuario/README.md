# Services: usuário

## Casos de uso

- `CadastrarUsuario`: normaliza e-mail, garante unicidade e gera hash BCrypt.
- `AutenticarUsuario`: valida credenciais e emite access/refresh token.
- `RenovarSessao`: rotaciona refresh token e detecta reutilização.
- `EncerrarSessao`: revoga o token ou família correspondente.
- `SolicitarRecuperacaoSenha`: cria token temporário sem revelar se o e-mail existe.
- `RedefinirSenha`: valida uso único, altera a senha e revoga sessões existentes.

## Resultado esperado

- Access token proposto com 15 minutos.
- Refresh token rotativo, persistido somente em hash.
- Reutilização de refresh token revoga sua família.
- Token inválido, expirado, usado ou revogado é rejeitado.
