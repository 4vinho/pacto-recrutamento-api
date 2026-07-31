package br.com.pacto.recrutamento.app.ports.candidato;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface CandidatoRepository {
    boolean existePorUsuarioId(UUID usuarioId);
    CandidatoPersistido salvar(UUID usuarioId, LocalDate dataAdmissao);
    Optional<CandidatoPersistido> buscarPorUsuarioId(UUID usuarioId);
    CandidatoPersistido atualizar(CandidatoPersistido candidato, LocalDate dataAdmissao);
    PaginaCandidaturas listarCandidaturasDoUsuario(UUID usuarioId, int page, int pageSize);
}
