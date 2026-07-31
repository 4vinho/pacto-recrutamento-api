package br.com.pacto.recrutamento.app.ports.out.usuario;

import br.com.pacto.recrutamento.core.entities.Usuario;

import java.time.OffsetDateTime;

public interface GeradorTokenPort {
    String gerarAccessToken(Usuario usuario, OffsetDateTime expiraEm);

    String gerarTokenAleatorio();

    String calcularHash(String token);
}
