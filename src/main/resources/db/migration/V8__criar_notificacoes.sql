CREATE TABLE notificacoes (
    id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    mensagem TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    tentativas INTEGER NOT NULL DEFAULT 0,
    lida_em TIMESTAMPTZ,
    criado_em TIMESTAMPTZ NOT NULL,
    ultimo_erro TEXT,
    CONSTRAINT pk_notificacoes PRIMARY KEY (id),
    CONSTRAINT ck_notificacoes_tipo CHECK (tipo IN ('CANDIDATURA_CRIADA', 'STATUS_CANDIDATURA_ALTERADO')),
    CONSTRAINT ck_notificacoes_status CHECK (status IN ('PENDENTE', 'ENVIADA', 'FALHA')),
    CONSTRAINT ck_notificacoes_tentativas CHECK (tentativas >= 0),
    CONSTRAINT fk_notificacoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

CREATE INDEX idx_notificacoes_usuario_id ON notificacoes (usuario_id);
CREATE INDEX idx_notificacoes_status ON notificacoes (status);
