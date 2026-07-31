ALTER TABLE curriculos
    ADD CONSTRAINT ck_curriculos_tamanho_maximo
    CHECK (tamanho_bytes <= 5242880);
