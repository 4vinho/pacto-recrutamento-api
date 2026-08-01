package br.com.pacto.recrutamento.app.usecases.candidatura;

import br.com.pacto.recrutamento.app.dtos.candidatura.CandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.HistoricoCandidaturaDTO;
import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.PerguntaCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.RequisitoCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.VagaCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.usuario.UsuarioPort;
import br.com.pacto.recrutamento.core.entities.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
class CandidaturaDtoMapper {
    private final CandidaturaPort candidaturas;
    private final VagaCandidaturaPort vagas;
    private final PerguntaCandidaturaPort perguntas;
    private final RequisitoCandidaturaPort requisitos;
    private final UsuarioPort usuarios;

    CandidaturaDtoMapper(CandidaturaPort candidaturas, VagaCandidaturaPort vagas,
            PerguntaCandidaturaPort perguntas, RequisitoCandidaturaPort requisitos,
            UsuarioPort usuarios) {
        this.candidaturas = candidaturas;
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.usuarios = usuarios;
    }

    CandidaturaDTO paraDto(Candidatura candidatura) {
        String titulo = vagas.buscarPorId(candidatura.getVagaId()).map(Vaga::getTitulo).orElse(null);
        Usuario candidato = usuarios.buscarPorId(candidatura.getUsuarioId()).orElse(null);
        return new CandidaturaDTO(candidatura.getId(), candidatura.getUsuarioId(),
                candidato == null ? null : candidato.getNome(),
                candidato == null ? null : candidato.getEmail(),
                candidato == null ? null : candidato.getTelefone(), candidatura.getVagaId(),
                titulo, candidatura.getStatus(), candidatura.getCriadoEm(),
                candidatura.isPerguntasRespondidas(), candidatura.isRequisitosRespondidos(),
                Collections.<CandidaturaDTO.RespostaDetalhe>emptyList(),
                Collections.<CandidaturaDTO.RequisitoDetalhe>emptyList(),
                Collections.<HistoricoCandidaturaDTO>emptyList(), null, candidatura.getVersao());
    }

    CandidaturaDTO paraDtoDetalhado(Candidatura candidatura) {
        return paraDtoDetalhado(candidatura, true);
    }

    CandidaturaDTO paraDtoDetalhadoCandidato(Candidatura candidatura) {
        return paraDtoDetalhado(candidatura, false);
    }

    private CandidaturaDTO paraDtoDetalhado(
            Candidatura candidatura, boolean incluirAvaliacaoInterna) {
        Map<UUID, String> perguntasPorId = new HashMap<>();
        for (PerguntaVaga pergunta : perguntas.listarAtivasPorVagaId(candidatura.getVagaId()))
            perguntasPorId.put(pergunta.getId(), pergunta.getEnunciado());
        List<CandidaturaDTO.RespostaDetalhe> respostas = new ArrayList<>();
        for (RespostaCandidatura resposta : candidaturas.listarRespostas(candidatura.getId()))
            respostas.add(new CandidaturaDTO.RespostaDetalhe(
                    perguntasPorId.getOrDefault(resposta.getPerguntaId(), "Pergunta"),
                    resposta.getValor()));

        Map<UUID, String> requisitosPorId = new HashMap<>();
        for (RequisitoVaga requisito : requisitos.listarAtivosPorVagaId(candidatura.getVagaId()))
            requisitosPorId.put(requisito.getId(), requisito.getDescricao());
        List<CandidaturaDTO.RequisitoDetalhe> requisitosDto = new ArrayList<>();
        for (RespostaRequisitoCandidatura resposta
                : candidaturas.listarRespostasRequisitos(candidatura.getId()))
            requisitosDto.add(new CandidaturaDTO.RequisitoDetalhe(
                    requisitosPorId.getOrDefault(resposta.getRequisitoId(), "Requisito"),
                    resposta.getNivel()));

        List<HistoricoCandidaturaDTO> historico = new ArrayList<>();
        String feedback = null;
        if (incluirAvaliacaoInterna) {
            for (HistoricoCandidatura item : candidaturas.listarHistorico(candidatura.getId())) {
                historico.add(new HistoricoCandidaturaDTO(item.getId(), item.getStatusAnterior(),
                        item.getNovoStatus(), item.getFeedback(), item.getCriadoEm()));
                if (item.getFeedback() != null && !item.getFeedback().isEmpty()) {
                    feedback = item.getFeedback();
                }
            }
        }
        CandidaturaDTO basico = paraDto(candidatura);
        return new CandidaturaDTO(basico.getId(), basico.getUsuarioId(), basico.getNomeCandidato(),
                basico.getEmailCandidato(), basico.getTelefoneCandidato(), basico.getVagaId(),
                basico.getTituloVaga(), basico.getStatus(), basico.getCriadaEm(),
                basico.isPerguntasRespondidas(), basico.isRequisitosRespondidos(), respostas,
                requisitosDto, historico, feedback, candidatura.getVersao());
    }
}
