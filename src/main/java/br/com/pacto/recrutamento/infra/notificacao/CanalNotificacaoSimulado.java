package br.com.pacto.recrutamento.infra.notificacao;

import br.com.pacto.recrutamento.app.notificacao.CanalNotificacao;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import org.springframework.stereotype.Component;

@Component
public class CanalNotificacaoSimulado implements CanalNotificacao {
    public void enviar(Notificacao notificacao) { }
}
