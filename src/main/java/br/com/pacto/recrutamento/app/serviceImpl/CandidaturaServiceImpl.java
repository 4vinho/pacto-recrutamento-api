package br.com.pacto.recrutamento.app.serviceImpl;

import br.com.pacto.recrutamento.app.ports.candidatura.AutorizacaoResponsavelCandidatura;
import br.com.pacto.recrutamento.app.ports.candidatura.CandidaturaRepositorio;
import br.com.pacto.recrutamento.app.ports.candidatura.EventosCandidatura;
import br.com.pacto.recrutamento.app.ports.candidatura.PerguntaCandidaturaRepositorio;
import br.com.pacto.recrutamento.app.ports.candidatura.VagaCandidaturaRepositorio;

import br.com.pacto.recrutamento.app.dtos.candidatura.AtualizarStatusCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.CancelarCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.CandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.ConsultarCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.CriarCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.RegistrarRespostasDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.RespostaCandidaturaDTO;
import br.com.pacto.recrutamento.app.ports.candidato.CandidatoPersistido;
import br.com.pacto.recrutamento.app.ports.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.services.CandidaturaService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class CandidaturaServiceImpl implements CandidaturaService {
    private final CandidatoRepository candidatos;
    private final CandidaturaRepositorio candidaturas;
    private final VagaCandidaturaRepositorio vagas;
    private final PerguntaCandidaturaRepositorio perguntas;
    private final AutorizacaoResponsavelCandidatura autorizacao;
    private final EventosCandidatura eventos;

    public CandidaturaServiceImpl(CandidatoRepository candidatos,
                                  CandidaturaRepositorio candidaturas,
                                  VagaCandidaturaRepositorio vagas,
                                  PerguntaCandidaturaRepositorio perguntas,
                                  AutorizacaoResponsavelCandidatura autorizacao,
                                  EventosCandidatura eventos) {
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
            return erro(400, "Dados da candidatura invalidos");
        }
        CandidatoPersistido candidato = candidatos.buscarPorUsuarioId(command.getUsuarioId()).orElse(null);
        if (candidato == null) {
            return erro(403, "Usuario nao possui perfil de candidato");
        }
        Vaga vaga = vagas.buscarPorId(command.getVagaId()).orElse(null);
        if (vaga == null) {
            return erro(404, "Vaga nao encontrada");
        }
        if (!vaga.aceitaCandidatura()) {
            return erro(422, "Vaga nao aceita candidaturas");
        }
        if (candidaturas.existePorCandidatoIdEVagaId(candidato.getId(), vaga.getId())) {
            return erro(409, "Candidato ja possui candidatura para esta vaga");
        }
        Candidatura candidatura = new Candidatura(candidato.getId(), vaga.getId());
        try {
            candidaturas.salvar(candidatura);
        } catch (CandidaturaRepositorio.CandidaturaDuplicadaException ex) {
            return erro(409, "Candidato ja possui candidatura para esta vaga");
        }
        publicarCriacao(candidatura);
        return new TypedResponse<>(201, "Candidatura criada", paraDto(candidatura));
    }

    @Override
    public TypedResponse<CandidaturaDTO> registrarRespostas(RegistrarRespostasDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null
                || command.getRespostas() == null || command.getRespostas().isEmpty()) {
            return erro(400, "Lote de respostas invalido");
        }
        Candidatura candidatura = candidaturas.buscarPorId(command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, "Candidatura nao encontrada");
        }
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, "Candidatura nao pertence ao candidato");
        }
        if (!estruturaDoLoteValida(command.getRespostas())) {
            return erro(400, "Lote de respostas invalido");
        }
        List<RespostaCandidatura> lote = validarLote(candidatura, command.getRespostas());
        if (lote == null) {
            return erro(422, "Lote de respostas incompativel com a vaga");
        }
        try {
            candidaturas.salvarRespostasAtomicamente(lote);
        } catch (CandidaturaRepositorio.RespostasDuplicadasException ex) {
            return erro(409, "Uma ou mais perguntas ja foram respondidas");
        }
        return new TypedResponse<>(200, "Respostas registradas", paraDto(candidatura));
    }

    @Override
    public TypedResponse<CandidaturaDTO> atualizarStatusCandidatura(AtualizarStatusCandidaturaDTO command) {
        if (command == null || command.getUsuarioSolicitanteId() == null || command.getCandidaturaId() == null
                || command.getStatus() == null) {
            return erro(400, "Dados de atualizacao invalidos");
        }
        Candidatura candidatura = candidaturas.buscarPorId(command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, "Candidatura nao encontrada");
        }
        Vaga vaga = vagas.buscarPorId(candidatura.getVagaId()).orElse(null);
        if (vaga == null) {
            return erro(404, "Vaga nao encontrada");
        }
        if (!autorizacao.podeGerenciar(command.getUsuarioSolicitanteId(), vaga)) {
            return erro(403, "Usuario nao autorizado a alterar a candidatura");
        }
        StatusCandidatura anterior = candidatura.getStatus();
        try {
            candidatura.setStatus(command.getStatus());
        } catch (IllegalArgumentException ex) {
            return erro(400, "Status da candidatura invalido");
        } catch (IllegalStateException ex) {
            return erro(422, "Transicao de candidatura invalida");
        }
        candidaturas.salvar(candidatura);
        publicarAlteracao(candidatura, anterior);
        return new TypedResponse<>(200, "Status da candidatura atualizado", paraDto(candidatura));
    }

    @Override
    public TypedResponse<CandidaturaDTO> cancelarCandidatura(CancelarCandidaturaDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null) {
            return erro(400, "Dados de cancelamento invalidos");
        }
        Candidatura candidatura = candidaturas.buscarPorId(command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, "Candidatura nao encontrada");
        }
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, "Candidatura nao pertence ao candidato");
        }
        if (candidatura.getStatus() != StatusCandidatura.ENVIADA
                && candidatura.getStatus() != StatusCandidatura.EM_ANALISE) {
            return erro(422, "Cancelamento nao permitido para o estado atual");
        }
        StatusCandidatura anterior = candidatura.getStatus();
        try {
            candidatura.cancelar(OffsetDateTime.now());
        } catch (IllegalArgumentException ex) {
            return erro(400, "Dados de cancelamento invalidos");
        } catch (IllegalStateException ex) {
            return erro(422, "Cancelamento nao permitido para o estado atual");
        }
        candidaturas.salvar(candidatura);
        publicarAlteracao(candidatura, anterior);
        return new TypedResponse<>(200, "Candidatura cancelada", paraDto(candidatura));
    }

    @Override
    public TypedResponse<CandidaturaDTO> consultarCandidatura(ConsultarCandidaturaDTO query) {
        if (query == null || query.getUsuarioSolicitanteId() == null || query.getCandidaturaId() == null) {
            return erro(400, "Consulta de candidatura invalida");
        }
        Candidatura candidatura = candidaturas.buscarPorId(query.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, "Candidatura nao encontrada");
        }
        if (pertenceAoUsuario(candidatura, query.getUsuarioSolicitanteId())) {
            return new TypedResponse<>(200, "Candidatura encontrada", paraDto(candidatura));
        }
        Vaga vaga = vagas.buscarPorId(candidatura.getVagaId()).orElse(null);
        if (vaga != null && autorizacao.podeGerenciar(query.getUsuarioSolicitanteId(), vaga)) {
            return new TypedResponse<>(200, "Candidatura encontrada", paraDto(candidatura));
        }
        return erro(403, "Usuario nao autorizado a consultar a candidatura");
    }

    private boolean pertenceAoUsuario(Candidatura candidatura, UUID usuarioId) {
        Optional<CandidatoPersistido> candidato = candidatos.buscarPorUsuarioId(usuarioId);
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
