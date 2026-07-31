package br.com.pacto.recrutamento.app.ports.out.notificacao;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.ports.out.notificacao.model.DestinatariosCandidatura;
import br.com.pacto.recrutamento.app.serviceImpl.NotificacaoServiceImpl;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.core.enums.StatusNotificacao;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacaoServiceImplTest {
    @Test
    void persistirIntencaoAntesDoCanalNotificaResponsavelECandidato() {
        Memoria memoria = new Memoria();
        UUID evento = UUID.randomUUID();
        NotificacaoServiceImpl service = new NotificacaoServiceImpl(memoria, memoria, memoria);

        TypedResponse<Void> response = service.processarCandidaturaCriada(new CandidaturaCriadaDTO(evento, memoria.candidaturaId, OffsetDateTime.now()));

        assertThat(response.getStatusCode()).isEqualTo(202);
        assertThat(memoria.statusesPersistidos).containsExactly(
                StatusNotificacao.PENDENTE, StatusNotificacao.ENVIADA,
                StatusNotificacao.PENDENTE, StatusNotificacao.ENVIADA);
    }

    @Test
    void falhaDoCanalPermaneceRegistradaSemPropagarExcecao() {
        Memoria memoria = new Memoria();
        memoria.falhar = true;
        NotificacaoServiceImpl service = new NotificacaoServiceImpl(memoria, memoria, memoria);

        TypedResponse<Void> response = service.processarCandidaturaCriada(new CandidaturaCriadaDTO(UUID.randomUUID(), memoria.candidaturaId, OffsetDateTime.now()));

        assertThat(response.getStatusCode()).isEqualTo(202);
        assertThat(memoria.statusesPersistidos).contains(StatusNotificacao.PENDENTE, StatusNotificacao.FALHA);
        assertThat(memoria.persistidas.get(1).getStatus()).isEqualTo(StatusNotificacao.FALHA);
        assertThat(memoria.persistidas.get(1).getUltimoErro()).doesNotContain("RuntimeException");
    }

    private static class Memoria implements DestinatariosCandidaturaPort, NotificacaoPort, CanalNotificacaoPort {
        private final UUID candidaturaId = UUID.randomUUID();
        private final Map<String, Notificacao> dados = new HashMap<>();
        private final List<Notificacao> persistidas = new ArrayList<>();
        private final List<StatusNotificacao> statusesPersistidos = new ArrayList<>();
        private boolean falhar;

        public Optional<DestinatariosCandidatura> buscarPorCandidatura(UUID id) {
            return Optional.of(new DestinatariosCandidatura(UUID.randomUUID(), UUID.randomUUID()));
        }

        public Optional<Notificacao> buscarPorEventoEDestinatario(UUID evento, UUID usuario) {
            return Optional.ofNullable(dados.get(evento + ":" + usuario));
        }

        public Notificacao salvar(Notificacao notificacao) {
            statusesPersistidos.add(notificacao.getStatus());
            persistidas.add(notificacao);
            dados.put(notificacao.getEventoId() + ":" + notificacao.getUsuarioId(), notificacao);
            return notificacao;
        }

        public void enviar(Notificacao notificacao) {
            if (falhar) throw new RuntimeException("senha=segredo");
        }
    }
}
