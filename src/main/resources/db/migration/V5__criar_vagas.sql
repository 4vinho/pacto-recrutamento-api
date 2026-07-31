CREATE TABLE vagas (
    id UUID NOT NULL,
    responsavel_id UUID NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'RASCUNHO',
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    excluido_em TIMESTAMPTZ,
    CONSTRAINT pk_vagas PRIMARY KEY (id),
    CONSTRAINT ck_vagas_status CHECK (status IN ('RASCUNHO', 'PUBLICADA', 'ENCERRADA', 'CANCELADA')),
    CONSTRAINT fk_vagas_responsavel FOREIGN KEY (responsavel_id) REFERENCES usuarios (id)
);

CREATE INDEX idx_vagas_responsavel_id ON vagas (responsavel_id);
CREATE INDEX idx_vagas_status ON vagas (status);
