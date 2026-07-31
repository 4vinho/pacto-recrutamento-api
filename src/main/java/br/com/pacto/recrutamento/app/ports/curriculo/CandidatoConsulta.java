package br.com.pacto.recrutamento.app.ports.curriculo;

import java.util.Optional;
import java.util.UUID;

public interface CandidatoConsulta {
    Optional<UUID> buscarIdPorUsuario(UUID usuarioId);

    boolean pertenceAoUsuario(UUID candidatoId, UUID usuarioId);
}
