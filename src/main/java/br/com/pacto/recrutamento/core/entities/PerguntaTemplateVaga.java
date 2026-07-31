package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.TipoResposta;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PerguntaTemplateVaga {
    private UUID id = UUID.randomUUID();
    private UUID templateVagaId;
    private String enunciado;
    private TipoResposta tipoResposta;
    private boolean obrigatoria;
    private int ordem;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;
    private OffsetDateTime excluidoEm;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTemplateVagaId() { return templateVagaId; }
    public void setTemplateVagaId(UUID templateVagaId) { this.templateVagaId = templateVagaId; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public TipoResposta getTipoResposta() { return tipoResposta; }
    public void setTipoResposta(TipoResposta tipoResposta) { this.tipoResposta = tipoResposta; }
    public boolean isObrigatoria() { return obrigatoria; }
    public void setObrigatoria(boolean obrigatoria) { this.obrigatoria = obrigatoria; }
    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { if (ordem <= 0) throw new IllegalArgumentException("A ordem da pergunta deve ser positiva"); this.ordem = ordem; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
