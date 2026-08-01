package br.com.pacto.recrutamento.infra.repositorys.notificacao;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.infra.projections.DestinatariosCandidaturaProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DestinatariosCandidaturaRepository extends Repository<Candidatura, UUID> {
    @Query(value = "select vr.usuario_id as responsavelId, ca.usuario_id as usuarioId "
            + "from candidaturas ca join vagas_responsaveis vr on vr.vaga_id = ca.vaga_id "
            + "where ca.id = :candidaturaId",
            nativeQuery = true)
    List<DestinatariosCandidaturaProjection> buscarPorCandidatura(@Param("candidaturaId") UUID candidaturaId);
}
