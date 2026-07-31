CREATE TABLE usuarios (
    id UUID NOT NULL,
    email VARCHAR(254) NOT NULL,
    telefone VARCHAR(20),
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    excluido_em TIMESTAMPTZ,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);

CREATE TABLE papeis (
    id UUID NOT NULL,
    nome VARCHAR(50) NOT NULL,
    CONSTRAINT pk_papeis PRIMARY KEY (id),
    CONSTRAINT uk_papeis_nome UNIQUE (nome),
    CONSTRAINT ck_papeis_nome CHECK (nome IN ('ADMINISTRADOR', 'RESPONSAVEL_VAGA', 'CANDIDATO'))
);

CREATE TABLE usuarios_papeis (
    usuario_id UUID NOT NULL,
    papel_id UUID NOT NULL,
    CONSTRAINT pk_usuarios_papeis PRIMARY KEY (usuario_id, papel_id),
    CONSTRAINT fk_usuarios_papeis_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_usuarios_papeis_papel FOREIGN KEY (papel_id) REFERENCES papeis (id)
);

CREATE INDEX idx_usuarios_papeis_papel_id ON usuarios_papeis (papel_id);

INSERT INTO papeis (id, nome) VALUES
    ('10000000-0000-0000-0000-000000000001', 'ADMINISTRADOR'),
    ('10000000-0000-0000-0000-000000000002', 'RESPONSAVEL_VAGA'),
    ('10000000-0000-0000-0000-000000000003', 'CANDIDATO');
