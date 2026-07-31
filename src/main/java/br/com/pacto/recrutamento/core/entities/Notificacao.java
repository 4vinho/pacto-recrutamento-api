package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusNotificacao;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes")
public class Notificacao extends Entidade {
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoNotificacao tipo;
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;
    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    private String mensagem;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusNotificacao status = StatusNotificacao.PENDENTE;
    @Column(name = "tentativas", nullable = false)
    private int tentativas;
    @Column(name = "lida_em")
    private OffsetDateTime lidaEm;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "ultimo_erro", columnDefinition = "TEXT")
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

    @PrePersist
    private void inicializarCriacao() {
        if (criadoEm == null) {
            criadoEm = OffsetDateTime.now();
        }
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
    public void setStatus(StatusNotificacao status) {
        if (status == null) {
            throw new IllegalArgumentException("O status da notificação é obrigatório");
        }
        this.status = status;
    }
    public int getTentativas() { return tentativas; }
    public void setTentativas(int tentativas) { this.tentativas = tentativas; }
    public OffsetDateTime getLidaEm() { return lidaEm; }
    public void setLidaEm(OffsetDateTime lidaEm) { this.lidaEm = lidaEm; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) {
        if (criadoEm == null) {
            throw new IllegalArgumentException("A data de criação da notificação é obrigatória");
        }
        this.criadoEm = criadoEm;
    }
    public String getUltimoErro() { return ultimoErro; }
    public void setUltimoErro(String ultimoErro) { this.ultimoErro = ultimoErro; }
}
