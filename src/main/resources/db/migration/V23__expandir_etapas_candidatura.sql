ALTER TABLE candidaturas DROP CONSTRAINT ck_candidaturas_status;

UPDATE candidaturas
SET status = 'TRIAGEM'
WHERE status = 'EM_ANALISE';

UPDATE historicos_candidatura
SET status_anterior = 'TRIAGEM'
WHERE status_anterior = 'EM_ANALISE';

UPDATE historicos_candidatura
SET novo_status = 'TRIAGEM'
WHERE novo_status = 'EM_ANALISE';

ALTER TABLE candidaturas
    ADD CONSTRAINT ck_candidaturas_status
        CHECK (status IN (
            'RASCUNHO',
            'ENVIADA',
            'TRIAGEM',
            'ENTREVISTA_COMPORTAMENTAL',
            'ENTREVISTA_TECNICA',
            'APROVADA',
            'REJEITADA',
            'CANCELADA'
        ));
