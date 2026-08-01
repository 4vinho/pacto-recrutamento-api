package br.com.pacto.recrutamento.app.dtos.curriculo;

import java.util.Arrays;
import java.util.UUID;

public class EnviarCurriculoDTO {
    private final UUID usuarioId;
    private final UUID candidaturaId;
    private final String nomeOriginal;
    private final String contentType;
    private final byte[] conteudo;

    public EnviarCurriculoDTO(UUID usuarioId, UUID candidaturaId, String nomeOriginal,
                              String contentType, byte[] conteudo) {
        this.usuarioId = usuarioId;
        this.candidaturaId = candidaturaId;
        this.nomeOriginal = nomeOriginal;
        this.contentType = contentType;
        this.conteudo = conteudo == null ? new byte[0] : Arrays.copyOf(conteudo, conteudo.length);
    }
    public UUID getCandidaturaId() { return candidaturaId; }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getConteudo() {
        return Arrays.copyOf(conteudo, conteudo.length);
    }
}
