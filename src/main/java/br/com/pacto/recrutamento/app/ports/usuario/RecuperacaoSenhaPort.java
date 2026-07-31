package br.com.pacto.recrutamento.app.ports.usuario;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RecuperacaoSenhaPort {
    void salvar(UUID usuarioId, String tokenHash, OffsetDateTime expiraEm);

    Optional<UUID> consumirTokenValido(String tokenHash, OffsetDateTime agora);
}
