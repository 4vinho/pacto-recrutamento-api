package br.com.pacto.recrutamento.app.ports.notificacao;
import br.com.pacto.recrutamento.core.entities.Notificacao;
public interface CanalNotificacao { void enviar(Notificacao notificacao); }
