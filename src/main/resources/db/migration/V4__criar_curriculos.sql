CREATE TABLE curriculos (
    id UUID NOT NULL,
    candidato_id UUID NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    nome_original VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    tamanho_bytes BIGINT NOT NULL,
    checksum_sha256 CHAR(64) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    excluido_em TIMESTAMPTZ,
    CONSTRAINT pk_curriculos PRIMARY KEY (id),
    CONSTRAINT uk_curriculos_storage_key UNIQUE (storage_key),
    CONSTRAINT ck_curriculos_tamanho_positivo CHECK (tamanho_bytes > 0),
    CONSTRAINT fk_curriculos_candidato FOREIGN KEY (candidato_id) REFERENCES candidatos (id)
);

CREATE INDEX idx_curriculos_candidato_id ON curriculos (candidato_id);
CREATE UNIQUE INDEX uk_curriculos_candidato_ativo
    ON curriculos (candidato_id) WHERE excluido_em IS NULL;
