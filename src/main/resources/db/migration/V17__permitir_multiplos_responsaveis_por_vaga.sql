CREATE TABLE vagas_responsaveis
(
    vaga_id    UUID NOT NULL,
    usuario_id UUID NOT NULL,
    CONSTRAINT pk_vagas_responsaveis PRIMARY KEY (vaga_id, usuario_id),
    CONSTRAINT fk_vagas_responsaveis_vaga FOREIGN KEY (vaga_id) REFERENCES vagas (id),
    CONSTRAINT fk_vagas_responsaveis_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

INSERT INTO vagas_responsaveis (vaga_id, usuario_id)
SELECT id, responsavel_id
FROM vagas;

CREATE INDEX idx_vagas_responsaveis_usuario_id ON vagas_responsaveis (usuario_id);

ALTER TABLE vagas DROP CONSTRAINT fk_vagas_responsavel;
DROP INDEX idx_vagas_responsavel_id;
ALTER TABLE vagas DROP COLUMN responsavel_id;
