ALTER TABLE candidaturas ADD COLUMN usuario_id UUID;
ALTER TABLE candidaturas ADD COLUMN curriculo_enviado BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE candidaturas ca
SET usuario_id = c.usuario_id
FROM candidatos c
WHERE c.id = ca.candidato_id;

ALTER TABLE candidaturas ALTER COLUMN usuario_id SET NOT NULL;
ALTER TABLE candidaturas DROP CONSTRAINT uk_candidaturas_candidato_vaga;
ALTER TABLE candidaturas DROP CONSTRAINT fk_candidaturas_candidato;
ALTER TABLE candidaturas DROP COLUMN candidato_id;
ALTER TABLE candidaturas
    ADD CONSTRAINT uk_candidaturas_usuario_vaga UNIQUE (usuario_id, vaga_id),
    ADD CONSTRAINT fk_candidaturas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id);
CREATE INDEX idx_candidaturas_usuario_id ON candidaturas (usuario_id);

ALTER TABLE curriculos ADD COLUMN candidatura_id UUID;

UPDATE curriculos cu
SET candidatura_id = ca.id
FROM candidatos c
JOIN candidaturas ca ON ca.usuario_id = c.usuario_id
WHERE c.id = cu.candidato_id;

UPDATE candidaturas ca
SET curriculo_enviado = TRUE
WHERE EXISTS (SELECT 1 FROM curriculos cu WHERE cu.candidatura_id = ca.id AND cu.excluido_em IS NULL);

DELETE FROM curriculos WHERE candidatura_id IS NULL;
ALTER TABLE curriculos ALTER COLUMN candidatura_id SET NOT NULL;
DROP INDEX uk_curriculos_candidato_ativo;
DROP INDEX idx_curriculos_candidato_id;
ALTER TABLE curriculos DROP CONSTRAINT fk_curriculos_candidato;
ALTER TABLE curriculos DROP COLUMN candidato_id;
ALTER TABLE curriculos
    ADD CONSTRAINT fk_curriculos_candidatura FOREIGN KEY (candidatura_id) REFERENCES candidaturas (id) ON DELETE CASCADE;
CREATE INDEX idx_curriculos_candidatura_id ON curriculos (candidatura_id);
CREATE UNIQUE INDEX uk_curriculos_candidatura_ativo
    ON curriculos (candidatura_id) WHERE excluido_em IS NULL;

DROP TABLE candidatos;
