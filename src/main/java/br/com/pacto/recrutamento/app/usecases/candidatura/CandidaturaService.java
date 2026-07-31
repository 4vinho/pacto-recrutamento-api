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
import org.springframework.transaction.annotation.Transactional;

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
    private final RequisitoCandidaturaPort requisitos;
    private final AutorizacaoResponsavelCandidaturaPort autorizacao;
    private final EventosCandidaturaPort eventos;

    public CandidaturaService(CandidatoPort candidatos,
                              CandidaturaPort candidaturas,
                              VagaCandidaturaPort vagas,
                              PerguntaCandidaturaPort perguntas,
                              RequisitoCandidaturaPort requisitos,
                              AutorizacaoResponsavelCandidaturaPort autorizacao,
                              EventosCandidaturaPort eventos) {
        this.candidatos = candidatos;
        this.candidaturas = candidaturas;
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.autorizacao = autorizacao;
        this.eventos = eventos;
    }

    @Override
    @Transactional
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
        boolean possuiPerguntas = !perguntas.listarAtivasPorVagaId(vaga.getId()).isEmpty();
        boolean possuiRequisitos = !requisitos.listarAtivosPorVagaId(vaga.getId()).isEmpty();
        candidatura.configurarEtapas(possuiPerguntas, possuiRequisitos);
        try {
            candidaturas.salvar(candidatura);
        } catch (CandidaturaPort.CandidaturaDuplicadaException ex) {
            return erro(409, CANDIDATURA_DUPLICADA);
        }
        if (candidatura.getStatus() == StatusCandidatura.ENVIADA) {
            publicarCriacao(candidatura);
        }
        String mensagem = candidatura.getStatus() == StatusCandidatura.RASCUNHO
                ? "Candidatura criada como rascunho" : "Candidatura criada";
        return new TypedResponse<>(201, mensagem, paraDto(candidatura));
    }

    @Override
    @Transactional
    public TypedResponse<CandidaturaDTO> registrarRespostas(RegistrarRespostasDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null
                || command.getRespostas() == null || command.getRespostas().isEmpty()) {
            return erro(400, LOTE_RESPOSTAS_INVALIDO);
        }
        Candidatura candidatura = candidaturas.buscarPorIdParaAtualizacao(
                command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, CANDIDATURA_NAO_PERTENCE_AO_CANDIDATO);
        }
        if (candidatura.getStatus() != StatusCandidatura.RASCUNHO) {
            return erro(422, TRANSICAO_CANDIDATURA_INVALIDA);
        }
        if (candidatura.isPerguntasRespondidas()) {
            return erro(409, PERGUNTAS_JA_RESPONDIDAS);
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
            candidaturas.registrarRespostasPerguntasAtomicamente(candidatura, lote);
        } catch (CandidaturaPort.RespostasDuplicadasException ex) {
            return erro(409, PERGUNTAS_JA_RESPONDIDAS);
        }
        if (candidatura.getStatus() == StatusCandidatura.ENVIADA) publicarCriacao(candidatura);
        return new TypedResponse<>(200, "Respostas registradas", paraDto(candidatura));
    }

    @Override
    @Transactional
    public TypedResponse<CandidaturaDTO> registrarRequisitos(RegistrarRequisitosDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null
                || command.getRespostas() == null || command.getRespostas().isEmpty()) {
            return erro(400, LOTE_REQUISITOS_INVALIDO);
        }
        Candidatura candidatura = candidaturas.buscarPorIdParaAtualizacao(
                command.getCandidaturaId()).orElse(null);
        if (candidatura == null) return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, CANDIDATURA_NAO_PERTENCE_AO_CANDIDATO);
        }
        if (candidatura.getStatus() != StatusCandidatura.RASCUNHO) {
            return erro(422, TRANSICAO_CANDIDATURA_INVALIDA);
        }
        if (candidatura.isRequisitosRespondidos()) {
            return erro(409, REQUISITOS_JA_RESPONDIDOS);
        }
        List<RequisitoVaga> requisitosDaVaga = requisitos.listarAtivosPorVagaId(
                candidatura.getVagaId());
        List<RespostaRequisitoCandidatura> lote = validarRequisitos(
                candidatura, command.getRespostas(), requisitosDaVaga);
        if (lote == null) return erro(422, LOTE_REQUISITOS_INCOMPATIVEL);
        try {
            candidaturas.registrarRespostasRequisitosAtomicamente(candidatura, lote);
        } catch (CandidaturaPort.RequisitosJaRespondidosException ex) {
            return erro(409, REQUISITOS_JA_RESPONDIDOS);
        }
        if (candidatura.getStatus() == StatusCandidatura.ENVIADA) publicarCriacao(candidatura);
        return new TypedResponse<>(200, "Requisitos registrados", paraDto(candidatura));
    }

    @Override
    @Transactional
    public TypedResponse<CandidaturaDTO> atualizarStatusCandidatura(AtualizarStatusCandidaturaDTO command) {
        if (command == null || command.getUsuarioSolicitanteId() == null || command.getCandidaturaId() == null
                || command.getStatus() == null) {
            return erro(400, DADOS_ATUALIZACAO_INVALIDOS);
        }
        Candidatura candidatura = candidaturas.buscarPorIdParaAtualizacao(
                command.getCandidaturaId()).orElse(null);
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
    @Transactional
    public TypedResponse<CandidaturaDTO> cancelarCandidatura(CancelarCandidaturaDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null) {
            return erro(400, DADOS_CANCELAMENTO_INVALIDOS);
        }
        Candidatura candidatura = candidaturas.buscarPorIdParaAtualizacao(
                command.getCandidaturaId()).orElse(null);
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
    @Transactional(readOnly = true)
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

    private List<RespostaRequisitoCandidatura> validarRequisitos(
            Candidatura candidatura, List<RespostaRequisitoCandidaturaDTO> respostas,
            List<RequisitoVaga> requisitosDaVaga) {
        Map<UUID, RequisitoVaga> requisitosPorId = new HashMap<>();
        for (RequisitoVaga requisito : requisitosDaVaga) requisitosPorId.put(requisito.getId(), requisito);
        Set<UUID> respondidos = new HashSet<>();
        List<RespostaRequisitoCandidatura> lote = new ArrayList<>();
        for (RespostaRequisitoCandidaturaDTO resposta : respostas) {
            if (resposta == null || resposta.getRequisitoId() == null || resposta.getNivel() == null
                    || !respondidos.add(resposta.getRequisitoId())
                    || !requisitosPorId.containsKey(resposta.getRequisitoId())) return null;
            lote.add(new RespostaRequisitoCandidatura(candidatura.getId(),
                    resposta.getRequisitoId(), resposta.getNivel()));
        }
        for (RequisitoVaga requisito : requisitosDaVaga) {
            if (requisito.isObrigatorio() && !respondidos.contains(requisito.getId())) return null;
        }
        return lote;
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
                candidatura.getStatus(), candidatura.getCriadoEm(), candidatura.isPerguntasRespondidas(),
                candidatura.isRequisitosRespondidos());
    }
}
