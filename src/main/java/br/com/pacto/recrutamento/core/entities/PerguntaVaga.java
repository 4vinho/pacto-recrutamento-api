package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "perguntas_vaga")
public class PerguntaVaga extends EntidadeAuditavel {
    @Column(name = "vaga_id", nullable = false)
    private UUID vagaId;
    @Column(name = "enunciado", nullable = false, columnDefinition = "TEXT")
    private String enunciado;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_resposta", nullable = false, length = 30)
    private TipoResposta tipoResposta;
    @Column(name = "obrigatoria", nullable = false)
    private boolean obrigatoria;
    @Column(name = "ordem", nullable = false)
    private int ordem;
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

    public PerguntaVaga() {}
    public UUID getVagaId() { return vagaId; }
    public void setVagaId(UUID vagaId) { this.vagaId = vagaId; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public TipoResposta getTipoResposta() { return tipoResposta; }
    public void setTipoResposta(TipoResposta tipoResposta) { this.tipoResposta = tipoResposta; }
    public boolean isObrigatoria() { return obrigatoria; }
    public void setObrigatoria(boolean obrigatoria) { this.obrigatoria = obrigatoria; }
    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) {
        if (ordem <= 0) {
            throw new IllegalArgumentException("A ordem da pergunta deve ser positiva");
        }
        this.ordem = ordem;
    }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
