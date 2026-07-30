package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PerguntaVaga extends EntidadeAuditavel {
    private UUID vagaId;
    private String enunciado;
    private TipoResposta tipoResposta;
    private boolean obrigatoria;
    private int ordem;
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
    public void setOrdem(int ordem) { this.ordem = ordem; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
