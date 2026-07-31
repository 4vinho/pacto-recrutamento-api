package br.com.pacto.recrutamento.app.ports.out.usuario;

import java.time.OffsetDateTime;

public interface CanalRecuperacaoSenhaPort {
    void enviar(String email, String token, OffsetDateTime expiraEm);
}
