package br.com.pacto.recrutamento.infra.templatevaga;

import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface TemplateVagaJpaRepository extends JpaRepository<TemplateVaga, UUID> {
    Optional<TemplateVaga> findByIdAndExcluidoEmIsNull(UUID id);
}
