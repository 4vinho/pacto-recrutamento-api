package br.com.pacto.recrutamento.app.curriculo;

import java.util.Optional;
import java.util.UUID;

public interface CandidatoConsulta {
    Optional<UUID> buscarIdPorUsuario(UUID usuarioId);
    boolean pertenceAoUsuario(UUID candidatoId, UUID usuarioId);
}
