CREATE TABLE tokens_recuperacao_senha
(
    id         UUID        NOT NULL,
    usuario_id UUID        NOT NULL,
    token_hash CHAR(64)    NOT NULL,
    expira_em  TIMESTAMPTZ NOT NULL,
    usado_em   TIMESTAMPTZ,
    criado_em  TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_tokens_recuperacao_senha PRIMARY KEY (id),
    CONSTRAINT uk_tokens_recuperacao_senha_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_tokens_recuperacao_senha_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);
CREATE INDEX idx_tokens_recuperacao_senha_usuario_id ON tokens_recuperacao_senha (usuario_id);
