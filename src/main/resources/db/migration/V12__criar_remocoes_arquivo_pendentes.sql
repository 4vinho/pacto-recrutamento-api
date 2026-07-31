CREATE TABLE remocoes_arquivo_pendentes (
    id UUID NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    motivo VARCHAR(500) NOT NULL,
    tentativas INTEGER NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL,
    ultima_tentativa_em TIMESTAMPTZ,
    CONSTRAINT pk_remocoes_arquivo_pendentes PRIMARY KEY (id),
    CONSTRAINT uk_remocoes_arquivo_pendentes_storage_key UNIQUE (storage_key),
    CONSTRAINT ck_remocoes_arquivo_pendentes_tentativas CHECK (tentativas >= 0)
);

CREATE INDEX idx_remocoes_arquivo_pendentes_criado_em
    ON remocoes_arquivo_pendentes (criado_em);
