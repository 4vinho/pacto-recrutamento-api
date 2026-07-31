package br.com.pacto.recrutamento.app.ports.usuario;

import br.com.pacto.recrutamento.core.entities.Usuario;

import java.time.OffsetDateTime;

public interface GeradorToken {
    String gerarAccessToken(Usuario usuario, OffsetDateTime expiraEm);

    String gerarTokenAleatorio();

    String calcularHash(String token);
}
