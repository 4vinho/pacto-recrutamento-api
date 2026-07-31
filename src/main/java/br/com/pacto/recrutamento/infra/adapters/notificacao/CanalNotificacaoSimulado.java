package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.ports.out.notificacao.CanalNotificacaoPort;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import org.springframework.stereotype.Component;

@Component
public class CanalNotificacaoSimulado implements CanalNotificacaoPort {
    public void enviar(Notificacao notificacao) {
    }
}
