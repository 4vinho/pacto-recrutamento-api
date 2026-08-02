package br.com.pacto.recrutamento.app.ports.out.notificacao;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.app.ports.out.notificacao.model.DestinatariosCandidatura;
import br.com.pacto.recrutamento.app.usecases.notificacao.NotificacaoService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.core.enums.StatusNotificacao;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacaoServiceTest {
    @Test
    void eventoJaEnviadoNaoDuplicaNotificacao() {
        Memoria memoria = new Memoria();
        UUID evento = UUID.randomUUID();
        NotificacaoService service = new NotificacaoService(memoria, memoria, memoria);
        CandidaturaCriadaDTO dto = new CandidaturaCriadaDTO(evento, memoria.candidaturaId, OffsetDateTime.now());

        service.processarCandidaturaCriada(dto);
        int enviosIniciais = memoria.envios;
        service.processarCandidaturaCriada(dto);

        assertThat(memoria.envios).isEqualTo(enviosIniciais);
    }

    @Test
    void persistirIntencaoAntesDoCanalNotificaResponsavelECandidato() {
        Memoria memoria = new Memoria();
        UUID evento = UUID.randomUUID();
        NotificacaoService service = new NotificacaoService(memoria, memoria, memoria);

        TypedResponse<Void> response = service.processarCandidaturaCriada(new CandidaturaCriadaDTO(evento, memoria.candidaturaId, OffsetDateTime.now()));

        assertThat(response.getStatusCode()).isEqualTo(202);
        assertThat(memoria.statusesPersistidos).containsExactly(
                StatusNotificacao.PENDENTE, StatusNotificacao.ENVIADA,
                StatusNotificacao.PENDENTE, StatusNotificacao.ENVIADA,
                StatusNotificacao.PENDENTE, StatusNotificacao.ENVIADA);
    }

    @Test
    void falhaDoCanalPermaneceRegistradaSemPropagarExcecao() {
        Memoria memoria = new Memoria();
        memoria.falhar = true;
        NotificacaoService service = new NotificacaoService(memoria, memoria, memoria);

        TypedResponse<Void> response = service.processarCandidaturaCriada(new CandidaturaCriadaDTO(UUID.randomUUID(), memoria.candidaturaId, OffsetDateTime.now()));

        assertThat(response.getStatusCode()).isEqualTo(202);
        assertThat(memoria.statusesPersistidos).contains(StatusNotificacao.PENDENTE, StatusNotificacao.FALHA);
        assertThat(memoria.persistidas.get(1).getStatus()).isEqualTo(StatusNotificacao.FALHA);
        assertThat(memoria.persistidas.get(1).getUltimoErro()).doesNotContain("RuntimeException");
    }

    @Test
    void emailDeCriacaoUsaNomeDoCandidatoETituloDaVaga() {
        Memoria memoria = new Memoria();
        NotificacaoService service = new NotificacaoService(memoria, memoria, memoria);

        service.processarCandidaturaCriada(new CandidaturaCriadaDTO(
                UUID.randomUUID(), memoria.candidaturaId, OffsetDateTime.now()));

        assertThat(memoria.persistidas).extracting(Notificacao::getMensagem)
                .allMatch(mensagem -> !mensagem.contains(memoria.candidaturaId.toString()))
                .anyMatch(mensagem -> mensagem.contains("Maria Silva")
                        && mensagem.contains("Desenvolvedor Java"));
    }

    @Test
    void emailDeStatusTraduzStatusEIncluiFeedback() {
        Memoria memoria = new Memoria();
        NotificacaoService service = new NotificacaoService(memoria, memoria, memoria);

        service.processarStatusCandidaturaAlterado(new StatusCandidaturaAlteradoDTO(
                UUID.randomUUID(), memoria.candidaturaId, StatusCandidatura.ENVIADA,
                StatusCandidatura.EM_ANALISE, OffsetDateTime.now(), "Bom desempenho tecnico."));

        assertThat(memoria.persistidas).extracting(Notificacao::getMensagem)
                .anyMatch(mensagem -> mensagem.contains("Desenvolvedor Java")
                        && mensagem.contains("Em analise")
                        && mensagem.contains("Bom desempenho tecnico.")
                        && !mensagem.contains(memoria.candidaturaId.toString()));
    }

    private static class Memoria implements DestinatariosCandidaturaPort, NotificacaoPort, CanalNotificacaoPort {
        private final UUID candidaturaId = UUID.randomUUID();
        private final List<UUID> responsaveisIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        private final UUID usuarioId = UUID.randomUUID();
        private final Map<String, Notificacao> dados = new HashMap<>();
        private final List<Notificacao> persistidas = new ArrayList<>();
        private final List<StatusNotificacao> statusesPersistidos = new ArrayList<>();
        private boolean falhar;
        private int envios;

        public Optional<DestinatariosCandidatura> buscarPorCandidatura(UUID id) {
            return Optional.of(new DestinatariosCandidatura(
                    responsaveisIds, usuarioId, "Desenvolvedor Java", "Maria Silva"));
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

        public List<Notificacao> buscarParaReprocessamento(OffsetDateTime limite, int maximoTentativas) {
            return Collections.emptyList();
        }

        public void enviar(Notificacao notificacao) {
            envios++;
            if (falhar) throw new RuntimeException("senha=segredo");
        }
    }
}
