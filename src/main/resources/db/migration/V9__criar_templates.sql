CREATE TABLE templates_vaga
(
    id             UUID         NOT NULL,
    responsavel_id UUID         NOT NULL,
    titulo         VARCHAR(150) NOT NULL,
    descricao      TEXT         NOT NULL,
    criado_em      TIMESTAMPTZ  NOT NULL,
    atualizado_em  TIMESTAMPTZ  NOT NULL,
    excluido_em    TIMESTAMPTZ,
    CONSTRAINT pk_templates_vaga PRIMARY KEY (id),
    CONSTRAINT fk_templates_vaga_responsavel FOREIGN KEY (responsavel_id) REFERENCES usuarios (id)
);

CREATE TABLE perguntas_template_vaga
(
    id               UUID        NOT NULL,
    template_vaga_id UUID        NOT NULL,
    enunciado        TEXT        NOT NULL,
    tipo_resposta    VARCHAR(30) NOT NULL,
    obrigatoria      BOOLEAN     NOT NULL DEFAULT FALSE,
    ordem            INTEGER     NOT NULL,
    criado_em        TIMESTAMPTZ NOT NULL,
    atualizado_em    TIMESTAMPTZ NOT NULL,
    excluido_em      TIMESTAMPTZ,
    CONSTRAINT pk_perguntas_template_vaga PRIMARY KEY (id),
    CONSTRAINT ck_perguntas_template_tipo CHECK (tipo_resposta IN
                                                 ('TEXTO', 'NUMERO', 'BOOLEANO', 'DATA', 'SELECAO_UNICA')),
    CONSTRAINT ck_perguntas_template_ordem CHECK (ordem > 0),
    CONSTRAINT fk_perguntas_template_vaga FOREIGN KEY (template_vaga_id) REFERENCES templates_vaga (id)
);

CREATE TABLE requisitos_template_vaga
(
    id               UUID        NOT NULL,
    template_vaga_id UUID        NOT NULL,
    descricao        TEXT        NOT NULL,
    obrigatorio      BOOLEAN     NOT NULL DEFAULT FALSE,
    criado_em        TIMESTAMPTZ NOT NULL,
    atualizado_em    TIMESTAMPTZ NOT NULL,
    excluido_em      TIMESTAMPTZ,
    CONSTRAINT pk_requisitos_template_vaga PRIMARY KEY (id),
    CONSTRAINT fk_requisitos_template_vaga FOREIGN KEY (template_vaga_id) REFERENCES templates_vaga (id)
);

CREATE INDEX idx_templates_vaga_responsavel_id ON templates_vaga (responsavel_id);
CREATE INDEX idx_perguntas_template_vaga_id ON perguntas_template_vaga (template_vaga_id);
CREATE INDEX idx_requisitos_template_vaga_id ON requisitos_template_vaga (template_vaga_id);
