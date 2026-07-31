package br.com.pacto.recrutamento.app.ports.out.notificacao;

import br.com.pacto.recrutamento.core.entities.Notificacao;

public interface CanalNotificacaoPort {
    void enviar(Notificacao notificacao);
}
