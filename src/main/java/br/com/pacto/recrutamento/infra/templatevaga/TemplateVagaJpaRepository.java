package br.com.pacto.recrutamento.infra.templatevaga;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

interface TemplateVagaJpaRepository extends JpaRepository<TemplateVagaJpaEntity, UUID> {
    Optional<TemplateVagaJpaEntity> findByIdAndExcluidoEmIsNull(UUID id);
}
