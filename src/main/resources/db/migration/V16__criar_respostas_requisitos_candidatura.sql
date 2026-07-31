ALTER TABLE candidaturas
    ADD COLUMN perguntas_respondidas BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN requisitos_respondidos BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE candidaturas
SET perguntas_respondidas = status <> 'RASCUNHO' OR NOT EXISTS (
        SELECT 1 FROM perguntas_vaga p
        WHERE p.vaga_id = candidaturas.vaga_id AND p.excluido_em IS NULL
    ),
    requisitos_respondidos = status <> 'RASCUNHO' OR NOT EXISTS (
        SELECT 1 FROM requisitos_vaga r
        WHERE r.vaga_id = candidaturas.vaga_id AND r.excluido_em IS NULL
    );

CREATE TABLE respostas_requisitos_candidatura
(
    id             UUID        NOT NULL,
    candidatura_id UUID        NOT NULL,
    requisito_id   UUID        NOT NULL,
    nivel          VARCHAR(20) NOT NULL,
    criado_em      TIMESTAMPTZ NOT NULL,
    atualizado_em  TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_respostas_requisitos_candidatura PRIMARY KEY (id),
    CONSTRAINT uk_respostas_requisitos_candidatura_requisito
        UNIQUE (candidatura_id, requisito_id),
    CONSTRAINT ck_respostas_requisitos_candidatura_nivel
        CHECK (nivel IN ('MUITO_BAIXO', 'BAIXO', 'ALTO', 'MUITO_ALTO')),
    CONSTRAINT fk_respostas_requisitos_candidatura_candidatura
        FOREIGN KEY (candidatura_id) REFERENCES candidaturas (id),
    CONSTRAINT fk_respostas_requisitos_candidatura_requisito
        FOREIGN KEY (requisito_id) REFERENCES requisitos_vaga (id)
);

CREATE INDEX idx_respostas_requisitos_candidatura_requisito
    ON respostas_requisitos_candidatura (requisito_id);
