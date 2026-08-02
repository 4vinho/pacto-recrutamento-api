package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.app.ports.in.notificacao.NotificacaoUseCase;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

class EventosCandidaturaListenerTest {
    private final NotificacaoUseCase useCase = mock(NotificacaoUseCase.class);
    private final EventosCandidaturaListener listener = new EventosCandidaturaListener(useCase);

    @Test
    void encaminhaEventosPublicadosAoCasoDeUso() {
        CandidaturaCriadaDTO criada = new CandidaturaCriadaDTO(
                UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now());
        StatusCandidaturaAlteradoDTO alterada = new StatusCandidaturaAlteradoDTO(
                UUID.randomUUID(), UUID.randomUUID(), StatusCandidatura.ENVIADA,
                StatusCandidatura.TRIAGEM, OffsetDateTime.now());

        listener.candidaturaCriada(criada);
        listener.statusAlterado(alterada);

        verify(useCase).processarCandidaturaCriada(criada);
        verify(useCase).processarStatusCandidaturaAlterado(alterada);
    }

    @Test
    void falhaDeNotificacaoNaoPropagaParaFluxoConfirmado() {
        CandidaturaCriadaDTO evento = new CandidaturaCriadaDTO(
                UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now());
        when(useCase.processarCandidaturaCriada(evento)).thenThrow(new RuntimeException("falha"));

        listener.candidaturaCriada(evento);

        verify(useCase).processarCandidaturaCriada(evento);
    }
}
