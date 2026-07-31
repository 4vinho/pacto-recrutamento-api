package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.ports.out.notificacao.CanalNotificacaoPort;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CanalNotificacaoSimulado implements CanalNotificacaoPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(CanalNotificacaoSimulado.class);

    public void enviar(Notificacao notificacao) {
        LOGGER.info("[SIMULADO] Notificacao enviada: destinatarioId={}, tipo={}, titulo={}, mensagem={}",
                notificacao.getUsuarioId(), notificacao.getTipo(), notificacao.getTitulo(),
                notificacao.getMensagem());
    }
}
