package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "perguntas_template_vaga")
public class PerguntaTemplateVaga extends EntidadeAuditavel {
    @Column(name = "template_vaga_id", nullable = false)
    private UUID templateVagaId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_resposta", nullable = false)
    private TipoResposta tipoResposta;
    @Column(nullable = false)
    private boolean obrigatoria;
    @Column(nullable = false)
    private int ordem;
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

    public UUID getTemplateVagaId() {
        return templateVagaId;
    }

    public void setTemplateVagaId(UUID templateVagaId) {
        this.templateVagaId = templateVagaId;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public TipoResposta getTipoResposta() {
        return tipoResposta;
    }

    public void setTipoResposta(TipoResposta tipoResposta) {
        this.tipoResposta = tipoResposta;
    }

    public boolean isObrigatoria() {
        return obrigatoria;
    }

    public void setObrigatoria(boolean obrigatoria) {
        this.obrigatoria = obrigatoria;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        if (ordem <= 0) throw new IllegalArgumentException("A ordem da pergunta deve ser positiva");
        this.ordem = ordem;
    }

    public OffsetDateTime getExcluidoEm() {
        return excluidoEm;
    }

    public void setExcluidoEm(OffsetDateTime excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
