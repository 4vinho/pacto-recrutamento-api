package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.app.ports.in.notificacao.NotificacaoUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EventosCandidaturaListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventosCandidaturaListener.class);
    private final NotificacaoUseCase notificacoes;

    public EventosCandidaturaListener(NotificacaoUseCase notificacoes) {
        this.notificacoes = notificacoes;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void candidaturaCriada(CandidaturaCriadaDTO evento) {
        try {
            notificacoes.processarCandidaturaCriada(evento);
        } catch (RuntimeException exception) {
            LOGGER.error("Falha ao processar notificacoes da candidatura {}",
                    evento.getCandidaturaId(), exception);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void statusAlterado(StatusCandidaturaAlteradoDTO evento) {
        try {
            notificacoes.processarStatusCandidaturaAlterado(evento);
        } catch (RuntimeException exception) {
            LOGGER.error("Falha ao processar notificacao de status da candidatura {}",
                    evento.getCandidaturaId(), exception);
        }
    }
}
