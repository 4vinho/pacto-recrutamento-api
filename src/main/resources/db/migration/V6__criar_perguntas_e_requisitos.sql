CREATE TABLE perguntas_vaga (
    id UUID NOT NULL,
    vaga_id UUID NOT NULL,
    enunciado TEXT NOT NULL,
    tipo_resposta VARCHAR(30) NOT NULL,
    obrigatoria BOOLEAN NOT NULL DEFAULT FALSE,
    ordem INTEGER NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    excluido_em TIMESTAMPTZ,
    CONSTRAINT pk_perguntas_vaga PRIMARY KEY (id),
    CONSTRAINT ck_perguntas_vaga_tipo CHECK (tipo_resposta IN ('TEXTO', 'NUMERO', 'BOOLEANO', 'DATA', 'SELECAO_UNICA')),
    CONSTRAINT ck_perguntas_vaga_ordem_positiva CHECK (ordem > 0),
    CONSTRAINT fk_perguntas_vaga_vaga FOREIGN KEY (vaga_id) REFERENCES vagas (id)
);

CREATE TABLE requisitos_vaga (
    id UUID NOT NULL,
    vaga_id UUID NOT NULL,
    descricao TEXT NOT NULL,
    obrigatorio BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    excluido_em TIMESTAMPTZ,
    CONSTRAINT pk_requisitos_vaga PRIMARY KEY (id),
    CONSTRAINT fk_requisitos_vaga_vaga FOREIGN KEY (vaga_id) REFERENCES vagas (id)
);

CREATE INDEX idx_perguntas_vaga_vaga_id ON perguntas_vaga (vaga_id);
CREATE INDEX idx_requisitos_vaga_vaga_id ON requisitos_vaga (vaga_id);
