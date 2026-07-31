ALTER TABLE curriculos
    ALTER COLUMN checksum_sha256 TYPE VARCHAR(64);

ALTER TABLE tokens_recuperacao_senha
    ALTER COLUMN token_hash TYPE VARCHAR(64);
