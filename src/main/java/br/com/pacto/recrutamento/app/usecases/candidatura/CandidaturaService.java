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

import java.time.OffsetDateTime;
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
        try {
            candidaturas.salvar(candidatura);
        } catch (CandidaturaPort.CandidaturaDuplicadaException ex) {
            return erro(409, CANDIDATURA_DUPLICADA);
        }
        publicarCriacao(candidatura);
        return new TypedResponse<>(201, "Candidatura criada", paraDto(candidatura));
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
        if (!estruturaDoLoteValida(command.getRespostas())) {
            return erro(400, LOTE_RESPOSTAS_INVALIDO);
        }
        List<RespostaCandidatura> lote = validarLote(candidatura, command.getRespostas());
        if (lote == null) {
            return erro(422, LOTE_RESPOSTAS_INCOMPATIVEL);
        }
        try {
            candidaturas.salvarRespostasAtomicamente(lote);
        } catch (CandidaturaPort.RespostasDuplicadasException ex) {
            return erro(409, PERGUNTAS_JA_RESPONDIDAS);
        }
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
        if (candidatura.getStatus() != StatusCandidatura.ENVIADA
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

    private List<RespostaCandidatura> validarLote(Candidatura candidatura,
                                                  List<RespostaCandidaturaDTO> respostas) {
        List<RespostaCandidatura> lote = new ArrayList<>();
        for (RespostaCandidaturaDTO resposta : respostas) {
            if (!respostaValida(candidatura, resposta)) {
                return null;
            }
            lote.add(new RespostaCandidatura(candidatura.getId(), resposta.getPerguntaId(), resposta.getValor()));
        }
        return lote;
    }

    private boolean respostaValida(Candidatura candidatura, RespostaCandidaturaDTO resposta) {
        PerguntaVaga pergunta = perguntas.buscarAtivaPorId(resposta.getPerguntaId()).orElse(null);
        return pergunta != null && candidatura.getVagaId().equals(pergunta.getVagaId());
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
