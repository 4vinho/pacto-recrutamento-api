package br.com.pacto.recrutamento.infra.curriculo;

import br.com.pacto.recrutamento.app.curriculo.CurriculoRepositorio;
import br.com.pacto.recrutamento.core.entities.Curriculo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CurriculoRepositorioJpaAdapter implements CurriculoRepositorio {
    private final EntityManager entityManager;

    public CurriculoRepositorioJpaAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curriculo> buscarAtivoPorCandidato(UUID candidatoId) {
        List<Curriculo> encontrados = entityManager.createQuery(
                        "select c from Curriculo c where c.candidatoId = :candidatoId and c.excluidoEm is null",
                        Curriculo.class)
                .setParameter("candidatoId", candidatoId)
                .setMaxResults(1)
                .getResultList();
        return primeiro(encontrados);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curriculo> buscarAtivoPorId(UUID curriculoId) {
        List<Curriculo> encontrados = entityManager.createQuery(
                        "select c from Curriculo c where c.id = :id and c.excluidoEm is null",
                        Curriculo.class)
                .setParameter("id", curriculoId)
                .setMaxResults(1)
                .getResultList();
        return primeiro(encontrados);
    }

    @Override
    @Transactional
    public void salvar(Curriculo curriculo) {
        entityManager.persist(curriculo);
    }

    @Override
    @Transactional
    public void substituir(Curriculo anterior, Curriculo novo, OffsetDateTime excluidoEm) {
        anterior.setExcluidoEm(excluidoEm);
        entityManager.merge(anterior);
        entityManager.flush();
        entityManager.persist(novo);
    }

    private Optional<Curriculo> primeiro(List<Curriculo> encontrados) {
        return encontrados.isEmpty()
                ? Optional.<Curriculo>empty()
                : Optional.of(encontrados.get(0));
    }
}
