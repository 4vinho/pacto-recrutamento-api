package br.com.pacto.recrutamento.infra.notificacao;

import br.com.pacto.recrutamento.core.enums.StatusNotificacao;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes", uniqueConstraints = @UniqueConstraint(name = "uk_notificacoes_evento_usuario", columnNames = {"evento_id", "usuario_id"}))
public class NotificacaoJpaEntity {
    @Id private UUID id;
    @Column(name = "evento_id", nullable = false) private UUID eventoId;
    @Column(name = "usuario_id", nullable = false) private UUID usuarioId;
    @Enumerated(EnumType.STRING) private TipoNotificacao tipo;
    private String titulo;
    private String mensagem;
    @Enumerated(EnumType.STRING) private StatusNotificacao status;
    private int tentativas;
    @Column(name = "ultimo_erro") private String ultimoErro;
    @Column(name = "criado_em", nullable = false, updatable = false) private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private OffsetDateTime atualizadoEm;
    @PrePersist private void criar() { if (criadoEm == null) criadoEm = OffsetDateTime.now(); atualizadoEm = criadoEm; }
    @PreUpdate private void atualizar() { atualizadoEm = OffsetDateTime.now(); }
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getEventoId() { return eventoId; } public void setEventoId(UUID eventoId) { this.eventoId = eventoId; }
    public UUID getUsuarioId() { return usuarioId; } public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public TipoNotificacao getTipo() { return tipo; } public void setTipo(TipoNotificacao tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; } public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensagem() { return mensagem; } public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public StatusNotificacao getStatus() { return status; } public void setStatus(StatusNotificacao status) { this.status = status; }
    public int getTentativas() { return tentativas; } public void setTentativas(int tentativas) { this.tentativas = tentativas; }
    public String getUltimoErro() { return ultimoErro; } public void setUltimoErro(String ultimoErro) { this.ultimoErro = ultimoErro; }
    public OffsetDateTime getCriadoEm() { return criadoEm; } public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; } public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
