ALTER TABLE candidaturas
    DROP CONSTRAINT ck_candidaturas_status;

ALTER TABLE candidaturas
    ALTER COLUMN status SET DEFAULT 'RASCUNHO';

ALTER TABLE candidaturas
    ADD CONSTRAINT ck_candidaturas_status
        CHECK (status IN ('RASCUNHO', 'ENVIADA', 'EM_ANALISE', 'APROVADA', 'REJEITADA', 'CANCELADA'));
