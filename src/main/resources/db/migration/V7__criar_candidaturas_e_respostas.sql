CREATE TABLE candidaturas (
    id UUID NOT NULL,
    candidato_id UUID NOT NULL,
    vaga_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ENVIADA',
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    cancelado_em TIMESTAMPTZ,
    CONSTRAINT pk_candidaturas PRIMARY KEY (id),
    CONSTRAINT uk_candidaturas_candidato_vaga UNIQUE (candidato_id, vaga_id),
    CONSTRAINT ck_candidaturas_status CHECK (status IN ('ENVIADA', 'EM_ANALISE', 'APROVADA', 'REJEITADA', 'CANCELADA')),
    CONSTRAINT fk_candidaturas_candidato FOREIGN KEY (candidato_id) REFERENCES candidatos (id),
    CONSTRAINT fk_candidaturas_vaga FOREIGN KEY (vaga_id) REFERENCES vagas (id)
);

CREATE TABLE respostas_candidatura (
    id UUID NOT NULL,
    candidatura_id UUID NOT NULL,
    pergunta_id UUID NOT NULL,
    valor TEXT NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_respostas_candidatura PRIMARY KEY (id),
    CONSTRAINT uk_respostas_candidatura_pergunta UNIQUE (candidatura_id, pergunta_id),
    CONSTRAINT fk_respostas_candidatura_candidatura FOREIGN KEY (candidatura_id) REFERENCES candidaturas (id),
    CONSTRAINT fk_respostas_candidatura_pergunta FOREIGN KEY (pergunta_id) REFERENCES perguntas_vaga (id)
);

CREATE INDEX idx_candidaturas_vaga_id ON candidaturas (vaga_id);
CREATE INDEX idx_candidaturas_status ON candidaturas (status);
CREATE INDEX idx_respostas_candidatura_pergunta_id ON respostas_candidatura (pergunta_id);
