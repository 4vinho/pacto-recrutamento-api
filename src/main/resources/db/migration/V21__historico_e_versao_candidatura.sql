ALTER TABLE candidaturas
    ADD COLUMN versao BIGINT NOT NULL DEFAULT 0;

ALTER TABLE usuarios ADD COLUMN data_admissao DATE DEFAULT CURRENT_DATE;
UPDATE usuarios SET data_admissao = CURRENT_DATE WHERE data_admissao IS NULL;
ALTER TABLE usuarios ALTER COLUMN data_admissao SET NOT NULL;

CREATE TABLE historicos_candidatura (
    id UUID PRIMARY KEY,
    candidatura_id UUID NOT NULL,
    autor_id UUID NOT NULL,
    status_anterior VARCHAR(30),
    novo_status VARCHAR(30) NOT NULL,
    feedback VARCHAR(2000),
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_historicos_candidatura_candidatura
        FOREIGN KEY (candidatura_id) REFERENCES candidaturas(id),
    CONSTRAINT fk_historicos_candidatura_autor
        FOREIGN KEY (autor_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_historicos_candidatura_data
    ON historicos_candidatura(candidatura_id, criado_em);
