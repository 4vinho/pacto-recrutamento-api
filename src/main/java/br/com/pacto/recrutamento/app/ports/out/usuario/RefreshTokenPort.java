package br.com.pacto.recrutamento.app.ports.out.usuario;

import br.com.pacto.recrutamento.core.entities.RefreshToken;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenPort {
    Optional<RefreshToken> buscarPorHash(String hash);

    void salvar(RefreshToken token);

    void revogarFamilia(UUID familiaId, OffsetDateTime data);

    void revogarTodosDoUsuario(UUID usuarioId, OffsetDateTime data);
}
