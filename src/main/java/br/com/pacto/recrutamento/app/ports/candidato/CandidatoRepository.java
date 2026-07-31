package br.com.pacto.recrutamento.app.ports.candidato;

import br.com.pacto.recrutamento.core.entities.Candidato;

import java.util.Optional;
import java.util.UUID;

public interface CandidatoRepository {
    boolean existePorUsuarioId(UUID usuarioId);
    Candidato salvar(Candidato candidato);
    Optional<Candidato> buscarPorUsuarioId(UUID usuarioId);
    PaginaCandidaturas listarCandidaturasDoUsuario(UUID usuarioId, int page, int pageSize);
}
