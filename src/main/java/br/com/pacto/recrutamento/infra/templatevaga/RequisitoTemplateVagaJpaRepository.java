package br.com.pacto.recrutamento.infra.templatevaga;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface RequisitoTemplateVagaJpaRepository extends JpaRepository<RequisitoTemplateVagaJpaEntity, UUID> {
    Optional<RequisitoTemplateVagaJpaEntity> findByIdAndExcluidoEmIsNull(UUID id);
    List<RequisitoTemplateVagaJpaEntity> findByTemplateVagaIdAndExcluidoEmIsNull(UUID templateId);
}
