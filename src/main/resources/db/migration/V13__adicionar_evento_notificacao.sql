ALTER TABLE notificacoes
    ADD COLUMN evento_id UUID;
ALTER TABLE notificacoes
    ADD COLUMN atualizado_em TIMESTAMPTZ;
UPDATE notificacoes
SET evento_id = id
WHERE evento_id IS NULL;
UPDATE notificacoes
SET atualizado_em = criado_em
WHERE atualizado_em IS NULL;
ALTER TABLE notificacoes
    ALTER COLUMN evento_id SET NOT NULL;
ALTER TABLE notificacoes
    ALTER COLUMN atualizado_em SET NOT NULL;
ALTER TABLE notificacoes
    ADD CONSTRAINT uk_notificacoes_evento_usuario UNIQUE (evento_id, usuario_id);
