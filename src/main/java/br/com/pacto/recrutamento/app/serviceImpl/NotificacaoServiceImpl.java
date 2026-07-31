package br.com.pacto.recrutamento.app.serviceImpl;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.app.ports.notificacao.CanalNotificacao;
import br.com.pacto.recrutamento.app.ports.notificacao.DestinatariosCandidatura;
import br.com.pacto.recrutamento.app.ports.notificacao.DestinatariosCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.app.services.NotificacaoService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {
    private final DestinatariosCandidaturaPort destinatarios;
    private final NotificacaoPort notificacoes;
    private final CanalNotificacao canal;

    public NotificacaoServiceImpl(DestinatariosCandidaturaPort destinatarios, NotificacaoPort notificacoes, CanalNotificacao canal) {
        this.destinatarios = destinatarios;
        this.notificacoes = notificacoes;
        this.canal = canal;
    }

    public TypedResponse<Void> processarCandidaturaCriada(CandidaturaCriadaDTO evento) {
        if (evento == null || evento.getEventoId() == null || evento.getCandidaturaId() == null) {
            return resposta(400);
        }
        Optional<DestinatariosCandidatura> encontrados = destinatarios.buscarPorCandidatura(evento.getCandidaturaId());
        if (!encontrados.isPresent()) {
            return resposta(202);
        }
        for (UUID usuarioId : Arrays.asList(encontrados.get().getResponsavelId(), encontrados.get().getCandidatoId())) {
            processar(evento.getEventoId(), usuarioId, TipoNotificacao.CANDIDATURA_CRIADA, "Nova candidatura", "Uma candidatura foi criada.");
        }
        return resposta(202);
    }

    public TypedResponse<Void> processarStatusCandidaturaAlterado(StatusCandidaturaAlteradoDTO evento) {
        if (evento == null || evento.getEventoId() == null
                || evento.getCandidaturaId() == null || evento.getNovoStatus() == null) {
            return resposta(400);
        }
        Optional<DestinatariosCandidatura> encontrados = destinatarios.buscarPorCandidatura(evento.getCandidaturaId());
        if (!encontrados.isPresent()) {
            return resposta(202);
        }
        processar(evento.getEventoId(), encontrados.get().getCandidatoId(), TipoNotificacao.STATUS_CANDIDATURA_ALTERADO,
                "Status da candidatura atualizado", "O status da candidatura foi atualizado.");
        return resposta(202);
    }

    private void processar(UUID eventoId, UUID usuarioId, TipoNotificacao tipo, String titulo, String mensagem) {
        Optional<Notificacao> existente = notificacoes.buscarPorEventoEDestinatario(eventoId, usuarioId);
        Notificacao notificacao = existente.orElseGet(
                () -> notificacoes.salvar(new Notificacao(eventoId, usuarioId, tipo, titulo, mensagem))
        );
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
