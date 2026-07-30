package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusNotificacao;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Notificacao extends Entidade {
    private UUID usuarioId;
    private TipoNotificacao tipo;
    private String titulo;
    private String mensagem;
    private StatusNotificacao status = StatusNotificacao.PENDENTE;
    private int tentativas;
    private OffsetDateTime lidaEm;
    private OffsetDateTime criadoEm;
    private String ultimoErro;

    public Notificacao() {}
    public Notificacao(UUID usuarioId, TipoNotificacao tipo, String titulo, String mensagem) {
        super(UUID.randomUUID());
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.criadoEm = OffsetDateTime.now();
    }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public TipoNotificacao getTipo() { return tipo; }
    public void setTipo(TipoNotificacao tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public StatusNotificacao getStatus() { return status; }
    public void setStatus(StatusNotificacao status) { this.status = status; }
    public int getTentativas() { return tentativas; }
    public void setTentativas(int tentativas) { this.tentativas = tentativas; }
    public OffsetDateTime getLidaEm() { return lidaEm; }
    public void setLidaEm(OffsetDateTime lidaEm) { this.lidaEm = lidaEm; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public String getUltimoErro() { return ultimoErro; }
    public void setUltimoErro(String ultimoErro) { this.ultimoErro = ultimoErro; }
}
