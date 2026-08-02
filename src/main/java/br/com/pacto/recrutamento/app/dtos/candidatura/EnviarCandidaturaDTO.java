package br.com.pacto.recrutamento.app.dtos.candidatura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EnviarCandidaturaDTO {
    private final UUID usuarioId;
    private final UUID vagaId;
    private final List<RespostaCandidaturaDTO> respostas;
    private final List<RespostaRequisitoCandidaturaDTO> requisitos;

    public EnviarCandidaturaDTO(UUID usuarioId, UUID vagaId,
            List<RespostaCandidaturaDTO> respostas,
            List<RespostaRequisitoCandidaturaDTO> requisitos) {
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
        this.respostas = respostas == null ? null
                : Collections.unmodifiableList(new ArrayList<>(respostas));
        this.requisitos = requisitos == null ? null
                : Collections.unmodifiableList(new ArrayList<>(requisitos));
    }

    public UUID getUsuarioId() { return usuarioId; }
    public UUID getVagaId() { return vagaId; }
    public List<RespostaCandidaturaDTO> getRespostas() { return respostas; }
    public List<RespostaRequisitoCandidaturaDTO> getRequisitos() { return requisitos; }
}
