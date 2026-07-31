CREATE TABLE candidatos (
    id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    data_admissao DATE,
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_candidatos PRIMARY KEY (id),
    CONSTRAINT uk_candidatos_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_candidatos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);
