package br.com.pacto.recrutamento.app.dtos.curriculo;

import java.util.Arrays;
import java.util.UUID;

public class SubstituirCurriculoDTO {
    private final UUID usuarioId;
    private final String nomeOriginal;
    private final String contentType;
    private final byte[] conteudo;

    public SubstituirCurriculoDTO(UUID usuarioId, String nomeOriginal,
                                  String contentType, byte[] conteudo) {
        this.usuarioId = usuarioId;
        this.nomeOriginal = nomeOriginal;
        this.contentType = contentType;
        this.conteudo = Arrays.copyOf(conteudo, conteudo.length);
    }

    public UUID getUsuarioId() { return usuarioId; }
    public String getNomeOriginal() { return nomeOriginal; }
    public String getContentType() { return contentType; }
    public byte[] getConteudo() { return Arrays.copyOf(conteudo, conteudo.length); }
}
