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
        String vaga = valorOuPadrao(encontrados.get().getTituloVaga(), "vaga interna");
        String candidato = valorOuPadrao(encontrados.get().getNomeCandidato(), "Um candidato");
        for (UUID usuarioId : usuarios) {
            processar(evento.getEventoId(), usuarioId, TipoNotificacao.CANDIDATURA_CRIADA,
                    "Nova candidatura: " + vaga,
                    "Ola!\n\n" + candidato + " se candidatou a vaga \"" + vaga + "\".\n\n"
                            + "Acesse o painel de avaliacao para consultar o curriculo e as respostas.");
        }
        processar(evento.getEventoId(), encontrados.get().getUsuarioId(), TipoNotificacao.CANDIDATURA_CRIADA,
                "Candidatura recebida: " + vaga,
                "Ola, " + candidato + "!\n\nRecebemos sua candidatura para a vaga \"" + vaga + "\".\n\n"
                        + "Voce pode acompanhar as proximas etapas no painel de candidaturas.");
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
        String vaga = valorOuPadrao(encontrados.get().getTituloVaga(), "vaga interna");
        String candidato = valorOuPadrao(encontrados.get().getNomeCandidato(), "Candidato");
        String mensagem = "Ola, " + candidato + "!\n\nO status da sua candidatura para a vaga \""
                + vaga + "\" foi atualizado para \"" + descricaoStatus(evento.getNovoStatus()) + "\".";
        if (evento.getFeedback() != null && !evento.getFeedback().trim().isEmpty()) {
            mensagem += "\n\nFeedback da equipe:\n" + evento.getFeedback().trim();
        }
        mensagem += "\n\nConsulte o painel de candidaturas para acompanhar o processo.";
        processar(evento.getEventoId(), encontrados.get().getUsuarioId(),
                TipoNotificacao.STATUS_CANDIDATURA_ALTERADO,
                "Atualizacao da candidatura: " + vaga, mensagem);
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

    private String valorOuPadrao(String valor, String padrao) {
        return valor == null || valor.trim().isEmpty() ? padrao : valor.trim();
    }

    private String descricaoStatus(br.com.pacto.recrutamento.core.enums.StatusCandidatura status) {
        switch (status) {
            case RASCUNHO: return "Rascunho";
            case ENVIADA: return "Enviada";
            case EM_ANALISE: return "Em analise";
            case APROVADA: return "Aprovada";
            case REJEITADA: return "Nao selecionada";
            case CANCELADA: return "Cancelada";
            default: throw new IllegalArgumentException("Status de candidatura invalido");
        }
    }
}
