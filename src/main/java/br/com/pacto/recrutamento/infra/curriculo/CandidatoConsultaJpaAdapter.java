package br.com.pacto.recrutamento.infra.curriculo;

import br.com.pacto.recrutamento.app.ports.curriculo.CandidatoConsulta;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CandidatoConsultaJpaAdapter implements CandidatoConsulta {
    private final EntityManager entityManager;

    public CandidatoConsultaJpaAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UUID> buscarIdPorUsuario(UUID usuarioId) {
        List<UUID> encontrados = entityManager.createQuery(
                        "select c.id from Candidato c where c.usuarioId = :usuarioId", UUID.class)
                .setParameter("usuarioId", usuarioId)
                .setMaxResults(1)
                .getResultList();
        return encontrados.isEmpty()
                ? Optional.<UUID>empty()
                : Optional.of(encontrados.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean pertenceAoUsuario(UUID candidatoId, UUID usuarioId) {
        Long quantidade = entityManager.createQuery(
                        "select count(c) from Candidato c where c.id = :candidatoId and c.usuarioId = :usuarioId",
                        Long.class)
                .setParameter("candidatoId", candidatoId)
                .setParameter("usuarioId", usuarioId)
                .getSingleResult();
        return quantidade > 0;
    }
}
