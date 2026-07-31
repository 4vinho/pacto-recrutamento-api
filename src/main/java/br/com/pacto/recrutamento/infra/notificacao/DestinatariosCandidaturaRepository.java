package br.com.pacto.recrutamento.infra.notificacao;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;
public interface DestinatariosCandidaturaRepository extends Repository<Candidatura, UUID> {
    @Query(value = "select v.responsavel_id as responsavelId, c.usuario_id as candidatoId from candidaturas ca join vagas v on v.id = ca.vaga_id join candidatos c on c.id = ca.candidato_id where ca.id = :candidaturaId", nativeQuery = true)
    Optional<DestinatariosCandidaturaProjection> buscarPorCandidatura(@Param("candidaturaId") UUID candidaturaId);
}
