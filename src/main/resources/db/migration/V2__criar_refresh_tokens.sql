CREATE TABLE refresh_tokens
(
    id          UUID         NOT NULL,
    usuario_id  UUID         NOT NULL,
    token_hash  VARCHAR(255) NOT NULL,
    familia_id  UUID         NOT NULL,
    expira_em   TIMESTAMPTZ  NOT NULL,
    usado_em    TIMESTAMPTZ,
    revogado_em TIMESTAMPTZ,
    criado_em   TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

CREATE INDEX idx_refresh_tokens_usuario_id ON refresh_tokens (usuario_id);
CREATE INDEX idx_refresh_tokens_familia_id ON refresh_tokens (familia_id);
