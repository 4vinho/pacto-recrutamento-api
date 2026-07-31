package br.com.pacto.recrutamento.app.usecases.candidatura;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.app.dtos.candidatura.*;
import br.com.pacto.recrutamento.app.ports.in.candidatura.CandidaturaUseCase;
import br.com.pacto.recrutamento.app.ports.out.candidato.CandidatoPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.*;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class CandidaturaService implements CandidaturaUseCase {
    private final CandidatoPort candidatos;
    private final CandidaturaPort candidaturas;
    private final VagaCandidaturaPort vagas;
    private final PerguntaCandidaturaPort perguntas;
    private final AutorizacaoResponsavelCandidaturaPort autorizacao;
    private final EventosCandidaturaPort eventos;

    public CandidaturaService(CandidatoPort candidatos,
                              CandidaturaPort candidaturas,
                              VagaCandidaturaPort vagas,
                              PerguntaCandidaturaPort perguntas,
                              AutorizacaoResponsavelCandidaturaPort autorizacao,
                              EventosCandidaturaPort eventos) {
        this.candidatos = candidatos;
        this.candidaturas = candidaturas;
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.autorizacao = autorizacao;
        this.eventos = eventos;
    }

    @Override
    public TypedResponse<CandidaturaDTO> criarCandidatura(CriarCandidaturaDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getVagaId() == null) {
            return erro(400, DADOS_CANDIDATURA_INVALIDOS);
        }
        Candidato candidato = candidatos.buscarPorUsuarioId(command.getUsuarioId()).orElse(null);
        if (candidato == null) {
            return erro(403, USUARIO_SEM_PERFIL_CANDIDATO);
        }
        Vaga vaga = vagas.buscarPorId(command.getVagaId()).orElse(null);
        if (vaga == null) {
            return erro(404, VAGA_NAO_ENCONTRADA);
        }
        if (!vaga.aceitaCandidatura()) {
            return erro(422, VAGA_NAO_ACEITA_CANDIDATURAS);
        }
        if (candidaturas.existePorCandidatoIdEVagaId(candidato.getId(), vaga.getId())) {
            return erro(409, CANDIDATURA_DUPLICADA);
        }
        Candidatura candidatura = new Candidatura(candidato.getId(), vaga.getId());
        boolean exigeRespostas = !perguntas.listarAtivasPorVagaId(vaga.getId()).isEmpty();
        if (!exigeRespostas) {
            candidatura.setStatus(StatusCandidatura.ENVIADA);
        }
        try {
            candidaturas.salvar(candidatura);
        } catch (CandidaturaPort.CandidaturaDuplicadaException ex) {
            return erro(409, CANDIDATURA_DUPLICADA);
        }
        if (!exigeRespostas) {
            publicarCriacao(candidatura);
        }
        String mensagem = exigeRespostas ? "Candidatura criada como rascunho"
                : "Candidatura criada";
        return new TypedResponse<>(201, mensagem, paraDto(candidatura));
    }

    @Override
    public TypedResponse<CandidaturaDTO> registrarRespostas(RegistrarRespostasDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null
                || command.getRespostas() == null || command.getRespostas().isEmpty()) {
            return erro(400, LOTE_RESPOSTAS_INVALIDO);
        }
        Candidatura candidatura = candidaturas.buscarPorId(command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, CANDIDATURA_NAO_PERTENCE_AO_CANDIDATO);
        }
        if (candidatura.getStatus() != StatusCandidatura.RASCUNHO) {
            return erro(422, TRANSICAO_CANDIDATURA_INVALIDA);
        }
        if (!estruturaDoLoteValida(command.getRespostas())) {
            return erro(400, LOTE_RESPOSTAS_INVALIDO);
        }
        List<PerguntaVaga> perguntasDaVaga = perguntas.listarAtivasPorVagaId(candidatura.getVagaId());
        List<RespostaCandidatura> lote = validarLote(
                candidatura, command.getRespostas(), perguntasDaVaga);
        if (lote == null) {
            return erro(422, LOTE_RESPOSTAS_INCOMPATIVEL);
        }
        try {
            candidaturas.finalizarComRespostasAtomicamente(candidatura, lote);
        } catch (CandidaturaPort.RespostasDuplicadasException ex) {
            return erro(409, PERGUNTAS_JA_RESPONDIDAS);
        }
        publicarCriacao(candidatura);
        return new TypedResponse<>(200, "Respostas registradas", paraDto(candidatura));
    }

    @Override
    public TypedResponse<CandidaturaDTO> atualizarStatusCandidatura(AtualizarStatusCandidaturaDTO command) {
        if (command == null || command.getUsuarioSolicitanteId() == null || command.getCandidaturaId() == null
                || command.getStatus() == null) {
            return erro(400, DADOS_ATUALIZACAO_INVALIDOS);
        }
        Candidatura candidatura = candidaturas.buscarPorId(command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        Vaga vaga = vagas.buscarPorId(candidatura.getVagaId()).orElse(null);
        if (vaga == null) {
            return erro(404, VAGA_NAO_ENCONTRADA);
        }
        if (!autorizacao.podeGerenciar(command.getUsuarioSolicitanteId(), vaga)) {
            return erro(403, USUARIO_NAO_AUTORIZADO_ALTERAR_CANDIDATURA);
        }
        StatusCandidatura anterior = candidatura.getStatus();
        try {
            candidatura.setStatus(command.getStatus());
        } catch (IllegalArgumentException ex) {
            return erro(400, STATUS_CANDIDATURA_INVALIDO);
        } catch (IllegalStateException ex) {
            return erro(422, TRANSICAO_CANDIDATURA_INVALIDA);
        }
        candidaturas.salvar(candidatura);
        publicarAlteracao(candidatura, anterior);
        return new TypedResponse<>(200, "Status da candidatura atualizado", paraDto(candidatura));
    }

    @Override
    public TypedResponse<CandidaturaDTO> cancelarCandidatura(CancelarCandidaturaDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null) {
            return erro(400, DADOS_CANCELAMENTO_INVALIDOS);
        }
        Candidatura candidatura = candidaturas.buscarPorId(command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, CANDIDATURA_NAO_PERTENCE_AO_CANDIDATO);
        }
        if (candidatura.getStatus() != StatusCandidatura.RASCUNHO
                && candidatura.getStatus() != StatusCandidatura.ENVIADA
                && candidatura.getStatus() != StatusCandidatura.EM_ANALISE) {
            return erro(422, CANCELAMENTO_NAO_PERMITIDO);
        }
        StatusCandidatura anterior = candidatura.getStatus();
        try {
            candidatura.cancelar(OffsetDateTime.now());
        } catch (IllegalArgumentException ex) {
            return erro(400, DADOS_CANCELAMENTO_INVALIDOS);
        } catch (IllegalStateException ex) {
            return erro(422, CANCELAMENTO_NAO_PERMITIDO);
        }
        candidaturas.salvar(candidatura);
        publicarAlteracao(candidatura, anterior);
        return new TypedResponse<>(200, "Candidatura cancelada", paraDto(candidatura));
    }

    @Override
    public TypedResponse<CandidaturaDTO> consultarCandidatura(ConsultarCandidaturaDTO query) {
        if (query == null || query.getUsuarioSolicitanteId() == null || query.getCandidaturaId() == null) {
            return erro(400, CONSULTA_CANDIDATURA_INVALIDA);
        }
        Candidatura candidatura = candidaturas.buscarPorId(query.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        if (pertenceAoUsuario(candidatura, query.getUsuarioSolicitanteId())) {
            return new TypedResponse<>(200, "Candidatura encontrada", paraDto(candidatura));
        }
        Vaga vaga = vagas.buscarPorId(candidatura.getVagaId()).orElse(null);
        if (vaga != null && autorizacao.podeGerenciar(query.getUsuarioSolicitanteId(), vaga)) {
            return new TypedResponse<>(200, "Candidatura encontrada", paraDto(candidatura));
        }
        return erro(403, USUARIO_NAO_AUTORIZADO_CONSULTAR_CANDIDATURA);
    }

    private boolean pertenceAoUsuario(Candidatura candidatura, UUID usuarioId) {
        Optional<Candidato> candidato = candidatos.buscarPorUsuarioId(usuarioId);
        return candidato.isPresent() && candidatura.getCandidatoId().equals(candidato.get().getId());
    }

    private List<RespostaCandidatura> validarLote(
            Candidatura candidatura, List<RespostaCandidaturaDTO> respostas,
            List<PerguntaVaga> perguntasDaVaga) {
        Map<UUID, PerguntaVaga> perguntasPorId = new HashMap<>();
        for (PerguntaVaga pergunta : perguntasDaVaga) {
            perguntasPorId.put(pergunta.getId(), pergunta);
        }
        List<RespostaCandidatura> lote = new ArrayList<>();
        Set<UUID> respondidas = new HashSet<>();
        for (RespostaCandidaturaDTO resposta : respostas) {
            PerguntaVaga pergunta = perguntasPorId.get(resposta.getPerguntaId());
            if (pergunta == null || !respostaValida(pergunta, resposta.getValor())) {
                return null;
            }
            respondidas.add(pergunta.getId());
            lote.add(new RespostaCandidatura(candidatura.getId(), pergunta.getId(),
                    resposta.getValor().trim()));
        }
        for (PerguntaVaga pergunta : perguntasDaVaga) {
            if (pergunta.isObrigatoria() && !respondidas.contains(pergunta.getId())) return null;
        }
        return lote;
    }

    private boolean respostaValida(PerguntaVaga pergunta, String valor) {
        if (valor == null || valor.trim().isEmpty()) return false;
        String normalizado = valor.trim();
        try {
            switch (pergunta.getTipoResposta()) {
                case NUMERO:
                    new BigDecimal(normalizado);
                    return true;
                case BOOLEANO:
                    return "true".equalsIgnoreCase(normalizado)
                            || "false".equalsIgnoreCase(normalizado);
                case DATA:
                    LocalDate.parse(normalizado);
                    return true;
                case TEXTO:
                case SELECAO_UNICA:
                    return true;
                default:
                    return false;
            }
        } catch (NumberFormatException | DateTimeParseException exception) {
            return false;
        }
    }

    private boolean estruturaDoLoteValida(List<RespostaCandidaturaDTO> respostas) {
        Set<UUID> perguntasDoLote = new HashSet<>();
        for (RespostaCandidaturaDTO resposta : respostas) {
            if (resposta == null || resposta.getPerguntaId() == null || resposta.getValor() == null
                    || !perguntasDoLote.add(resposta.getPerguntaId())) {
                return false;
            }
        }
        return true;
    }

    private void publicarCriacao(Candidatura candidatura) {
        try {
            eventos.candidaturaCriada(candidatura);
        } catch (RuntimeException ignored) {
            // O evento e posterior a confirmacao da candidatura e nao a desfaz.
        }
    }

    private void publicarAlteracao(Candidatura candidatura, StatusCandidatura anterior) {
        try {
            eventos.statusAlterado(candidatura, anterior);
        } catch (RuntimeException ignored) {
            // O evento e posterior a confirmacao da alteracao e nao a desfaz.
        }
    }

    private TypedResponse<CandidaturaDTO> erro(int status, String mensagem) {
        return new TypedResponse<>(status, mensagem, null);
    }

    private CandidaturaDTO paraDto(Candidatura candidatura) {
        return new CandidaturaDTO(candidatura.getId(), candidatura.getCandidatoId(), candidatura.getVagaId(),
                candidatura.getStatus(), candidatura.getCriadoEm());
    }
}
