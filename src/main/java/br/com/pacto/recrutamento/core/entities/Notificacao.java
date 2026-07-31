package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusNotificacao;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes", uniqueConstraints = @UniqueConstraint(
        name = "uk_notificacoes_evento_usuario", columnNames = {"evento_id", "usuario_id"}))
public class Notificacao {
    @Id
    private UUID id;
    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;
    private String titulo;
    private String mensagem;
    @Enumerated(EnumType.STRING)
    private StatusNotificacao status;
    private int tentativas;
    @Column(name = "ultimo_erro")
    private String ultimoErro;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected Notificacao() {
    }

    public Notificacao(UUID eventoId, UUID usuarioId, TipoNotificacao tipo, String titulo, String mensagem) {
        this(UUID.randomUUID(), eventoId, usuarioId, tipo, titulo, mensagem, StatusNotificacao.PENDENTE, 0, null);
    }
    public Notificacao(UUID usuarioId, TipoNotificacao tipo, String titulo, String mensagem) {
        this(UUID.randomUUID(), usuarioId, tipo, titulo, mensagem);
    }
    public Notificacao(UUID id, UUID eventoId, UUID usuarioId, TipoNotificacao tipo, String titulo, String mensagem,
                       StatusNotificacao status, int tentativas, String ultimoErro) {
        this.id = id; this.eventoId = eventoId; this.usuarioId = usuarioId; this.tipo = tipo;
        this.titulo = titulo; this.mensagem = mensagem; this.status = status; this.tentativas = tentativas; this.ultimoErro = ultimoErro;
    }
    @PrePersist
    private void criar() {
        if (criadoEm == null) {
            criadoEm = OffsetDateTime.now();
        }
        atualizadoEm = criadoEm;
    }
    @PreUpdate
    private void atualizar() { atualizadoEm = OffsetDateTime.now(); }
    public void registrarEnvio() { tentativas++; status = StatusNotificacao.ENVIADA; ultimoErro = null; }
    public void registrarFalha(String erro) { tentativas++; status = StatusNotificacao.FALHA; ultimoErro = erro; }
    public UUID getId() { return id; } public UUID getEventoId() { return eventoId; } public UUID getUsuarioId() { return usuarioId; }
    public TipoNotificacao getTipo() { return tipo; } public String getTitulo() { return titulo; } public String getMensagem() { return mensagem; }
    public StatusNotificacao getStatus() { return status; } public int getTentativas() { return tentativas; } public String getUltimoErro() { return ultimoErro; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
}
