package br.com.pacto.recrutamento.app.dtos.candidatura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RegistrarRequisitosDTO {
    private final UUID usuarioId;
    private final UUID candidaturaId;
    private final List<RespostaRequisitoCandidaturaDTO> respostas;

    public RegistrarRequisitosDTO(UUID usuarioId, UUID candidaturaId,
                                  List<RespostaRequisitoCandidaturaDTO> respostas) {
        this.usuarioId = usuarioId;
        this.candidaturaId = candidaturaId;
        this.respostas = respostas == null ? null
                : Collections.unmodifiableList(new ArrayList<>(respostas));
    }

    public UUID getUsuarioId() { return usuarioId; }
    public UUID getCandidaturaId() { return candidaturaId; }
    public List<RespostaRequisitoCandidaturaDTO> getRespostas() { return respostas; }
}
