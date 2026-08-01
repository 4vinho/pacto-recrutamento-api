package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.ports.in.notificacao.NotificacaoUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RetentativaNotificacaoScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetentativaNotificacaoScheduler.class);
    private final NotificacaoUseCase notificacoes;

    public RetentativaNotificacaoScheduler(NotificacaoUseCase notificacoes) {
        this.notificacoes = notificacoes;
    }

    @Scheduled(fixedDelayString = "${notification.retry.delay-ms:60000}")
    public void reprocessar() {
        try {
            notificacoes.reprocessarFalhas();
        } catch (RuntimeException exception) {
            LOGGER.error("Falha ao reprocessar notificacoes pendentes.", exception);
        }
    }
}
