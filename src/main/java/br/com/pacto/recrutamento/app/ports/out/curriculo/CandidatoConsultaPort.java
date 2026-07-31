package br.com.pacto.recrutamento.app.ports.out.curriculo;

import java.util.Optional;
import java.util.UUID;

public interface CandidatoConsultaPort {
    Optional<UUID> buscarIdPorUsuario(UUID usuarioId);

    boolean pertenceAoUsuario(UUID candidatoId, UUID usuarioId);
}
