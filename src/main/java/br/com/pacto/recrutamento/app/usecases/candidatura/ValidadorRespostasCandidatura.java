package br.com.pacto.recrutamento.app.usecases.candidatura;

import br.com.pacto.recrutamento.app.dtos.candidatura.RespostaCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.RespostaRequisitoCandidaturaDTO;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import br.com.pacto.recrutamento.core.entities.RespostaRequisitoCandidatura;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
class ValidadorRespostasCandidatura {
    boolean estruturaValida(List<RespostaCandidaturaDTO> respostas) {
        Set<UUID> perguntas = new HashSet<>();
        for (RespostaCandidaturaDTO resposta : respostas) {
            if (resposta == null || resposta.getPerguntaId() == null || resposta.getValor() == null
                    || !perguntas.add(resposta.getPerguntaId())) return false;
        }
        return true;
    }

    List<RespostaCandidatura> validarPerguntas(Candidatura candidatura,
            List<RespostaCandidaturaDTO> respostas, List<PerguntaVaga> perguntasDaVaga) {
        Map<UUID, PerguntaVaga> porId = new HashMap<>();
        for (PerguntaVaga pergunta : perguntasDaVaga) porId.put(pergunta.getId(), pergunta);
        List<RespostaCandidatura> lote = new ArrayList<>();
        Set<UUID> respondidas = new HashSet<>();
        for (RespostaCandidaturaDTO resposta : respostas) {
            PerguntaVaga pergunta = porId.get(resposta.getPerguntaId());
            if (pergunta == null || !valorValido(pergunta, resposta.getValor())) return null;
            respondidas.add(pergunta.getId());
            lote.add(new RespostaCandidatura(candidatura.getId(), pergunta.getId(),
                    resposta.getValor().trim()));
        }
        for (PerguntaVaga pergunta : perguntasDaVaga) {
            if (pergunta.isObrigatoria() && !respondidas.contains(pergunta.getId())) return null;
        }
        return lote;
    }

    List<RespostaRequisitoCandidatura> validarRequisitos(Candidatura candidatura,
            List<RespostaRequisitoCandidaturaDTO> respostas, List<RequisitoVaga> requisitosDaVaga) {
        Map<UUID, RequisitoVaga> porId = new HashMap<>();
        for (RequisitoVaga requisito : requisitosDaVaga) porId.put(requisito.getId(), requisito);
        Set<UUID> respondidos = new HashSet<>();
        List<RespostaRequisitoCandidatura> lote = new ArrayList<>();
        for (RespostaRequisitoCandidaturaDTO resposta : respostas) {
            if (resposta == null || resposta.getRequisitoId() == null || resposta.getNivel() == null
                    || !respondidos.add(resposta.getRequisitoId())
                    || !porId.containsKey(resposta.getRequisitoId())) return null;
            lote.add(new RespostaRequisitoCandidatura(candidatura.getId(),
                    resposta.getRequisitoId(), resposta.getNivel()));
        }
        for (RequisitoVaga requisito : requisitosDaVaga) {
            if (requisito.isObrigatorio() && !respondidos.contains(requisito.getId())) return null;
        }
        return lote;
    }

    private boolean valorValido(PerguntaVaga pergunta, String valor) {
        if (valor == null || valor.trim().isEmpty()) return false;
        String normalizado = valor.trim();
        try {
            switch (pergunta.getTipoResposta()) {
                case NUMERO: new BigDecimal(normalizado); return true;
                case BOOLEANO:
                    return "true".equalsIgnoreCase(normalizado)
                            || "false".equalsIgnoreCase(normalizado);
                case DATA: LocalDate.parse(normalizado); return true;
                case TEXTO:
                case SELECAO_UNICA: return true;
                default: return false;
            }
        } catch (NumberFormatException | DateTimeParseException exception) {
            return false;
        }
    }
}
