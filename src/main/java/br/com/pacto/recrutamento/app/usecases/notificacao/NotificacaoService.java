package br.com.pacto.recrutamento.app.usecases.notificacao;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.app.ports.in.notificacao.NotificacaoUseCase;
import br.com.pacto.recrutamento.app.ports.out.notificacao.CanalNotificacaoPort;
import br.com.pacto.recrutamento.app.ports.out.notificacao.DestinatariosCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.app.ports.out.notificacao.model.DestinatariosCandidatura;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificacaoService implements NotificacaoUseCase {
    private final DestinatariosCandidaturaPort destinatarios;
    private final NotificacaoPort notificacoes;
    private final CanalNotificacaoPort canal;
    @Value("${notification.retry.delay-ms:60000}")
    private long atrasoRetentativaMs = 60000;
    @Value("${notification.retry.max-attempts:5}")
    private int maximoTentativas = 5;

    public NotificacaoService(DestinatariosCandidaturaPort destinatarios, NotificacaoPort notificacoes, CanalNotificacaoPort canal) {
        this.destinatarios = destinatarios;
        this.notificacoes = notificacoes;
        this.canal = canal;
    }

    @Transactional
    public TypedResponse<Void> processarCandidaturaCriada(CandidaturaCriadaDTO evento) {
        if (evento == null || evento.getEventoId() == null || evento.getCandidaturaId() == null) {
            return resposta(400);
        }
        Optional<DestinatariosCandidatura> encontrados = destinatarios.buscarPorCandidatura(evento.getCandidaturaId());
        if (!encontrados.isPresent()) {
            return resposta(202);
        }
        LinkedHashSet<UUID> usuarios = new LinkedHashSet<>(encontrados.get().getResponsaveisIds());
        usuarios.add(encontrados.get().getUsuarioId());
        for (UUID usuarioId : usuarios) {
            processar(evento.getEventoId(), usuarioId, TipoNotificacao.CANDIDATURA_CRIADA,
                    "Nova candidatura", "A candidatura " + evento.getCandidaturaId() + " foi criada.");
        }
        return resposta(202);
    }

    @Transactional
    public TypedResponse<Void> processarStatusCandidaturaAlterado(StatusCandidaturaAlteradoDTO evento) {
        if (evento == null || evento.getEventoId() == null
                || evento.getCandidaturaId() == null || evento.getNovoStatus() == null) {
            return resposta(400);
        }
        Optional<DestinatariosCandidatura> encontrados = destinatarios.buscarPorCandidatura(evento.getCandidaturaId());
        if (!encontrados.isPresent()) {
            return resposta(202);
        }
        processar(evento.getEventoId(), encontrados.get().getUsuarioId(), TipoNotificacao.STATUS_CANDIDATURA_ALTERADO,
                "Status da candidatura atualizado", "A candidatura " + evento.getCandidaturaId()
                        + " foi atualizada para " + evento.getNovoStatus() + ".");
        return resposta(202);
    }

    private void processar(UUID eventoId, UUID usuarioId, TipoNotificacao tipo, String titulo, String mensagem) {
        Optional<Notificacao> existente = notificacoes.buscarPorEventoEDestinatario(eventoId, usuarioId);
        Notificacao notificacao = existente.orElseGet(
                () -> notificacoes.salvar(new Notificacao(eventoId, usuarioId, tipo, titulo, mensagem))
        );
        if (notificacao.getStatus() == br.com.pacto.recrutamento.core.enums.StatusNotificacao.ENVIADA) {
            return;
        }
        enviar(notificacao);
    }

    @Override
    @Transactional
    public void reprocessarFalhas() {
        OffsetDateTime limite = OffsetDateTime.now().minusNanos(atrasoRetentativaMs * 1_000_000);
        for (Notificacao notificacao : notificacoes.buscarParaReprocessamento(limite, maximoTentativas)) {
            enviar(notificacao);
        }
    }

    private void enviar(Notificacao notificacao) {
        try {
            canal.enviar(notificacao);
            notificacao.registrarEnvio();
        } catch (RuntimeException exception) {
            notificacao.registrarFalha("Falha ao enviar notificacao.");
        }
        notificacoes.salvar(notificacao);
    }

    private TypedResponse<Void> resposta(int status) {
        return new TypedResponse<>(status, "Notificacao processada.", null);
    }
}
