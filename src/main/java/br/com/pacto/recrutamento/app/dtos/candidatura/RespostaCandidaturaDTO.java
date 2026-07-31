package br.com.pacto.recrutamento.app.dtos.candidatura;

import java.util.UUID;

public class RespostaCandidaturaDTO {
    private final UUID perguntaId;
    private final String valor;

    public RespostaCandidaturaDTO(UUID perguntaId, String valor) {
        this.perguntaId = perguntaId;
        this.valor = valor;
    }

    public UUID getPerguntaId() {
        return perguntaId;
    }

    public String getValor() {
        return valor;
    }
}
