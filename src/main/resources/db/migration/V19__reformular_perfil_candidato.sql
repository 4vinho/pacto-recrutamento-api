ALTER TABLE candidatos
    ADD COLUMN titulo_profissional VARCHAR(120),
    ADD COLUMN resumo_profissional VARCHAR(1000),
    ADD COLUMN experiencia VARCHAR(2000),
    ADD COLUMN formacao VARCHAR(1000),
    ADD COLUMN habilidades VARCHAR(500);

ALTER TABLE candidatos DROP COLUMN data_admissao;
