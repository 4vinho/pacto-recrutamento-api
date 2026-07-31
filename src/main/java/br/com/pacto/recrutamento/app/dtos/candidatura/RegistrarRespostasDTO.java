package br.com.pacto.recrutamento.app.dtos.candidatura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RegistrarRespostasDTO {
    private final UUID usuarioId;
    private final UUID candidaturaId;
    private final List<RespostaCandidaturaDTO> respostas;

    public RegistrarRespostasDTO(UUID usuarioId, UUID candidaturaId,
                                 List<RespostaCandidaturaDTO> respostas) {
        this.usuarioId = usuarioId;
        this.candidaturaId = candidaturaId;
        this.respostas = Collections.unmodifiableList(new ArrayList<>(respostas));
    }

    public UUID getUsuarioId() { return usuarioId; }
    public UUID getCandidaturaId() { return candidaturaId; }
    public List<RespostaCandidaturaDTO> getRespostas() { return respostas; }
}
