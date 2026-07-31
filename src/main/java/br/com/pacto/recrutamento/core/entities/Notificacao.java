package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusNotificacao;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Notificacao {
    private final UUID id;
    private final UUID eventoId;
    private final UUID usuarioId;
    private final TipoNotificacao tipo;
    private final String titulo;
    private final String mensagem;
    private StatusNotificacao status;
    private int tentativas;
    private String ultimoErro;

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
    public void registrarEnvio() { tentativas++; status = StatusNotificacao.ENVIADA; ultimoErro = null; }
    public void registrarFalha(String erro) { tentativas++; status = StatusNotificacao.FALHA; ultimoErro = erro; }
    public UUID getId() { return id; } public UUID getEventoId() { return eventoId; } public UUID getUsuarioId() { return usuarioId; }
    public TipoNotificacao getTipo() { return tipo; } public String getTitulo() { return titulo; } public String getMensagem() { return mensagem; }
    public StatusNotificacao getStatus() { return status; } public int getTentativas() { return tentativas; } public String getUltimoErro() { return ultimoErro; }
}
