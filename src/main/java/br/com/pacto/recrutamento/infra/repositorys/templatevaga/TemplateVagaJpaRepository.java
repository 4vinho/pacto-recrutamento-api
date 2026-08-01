package br.com.pacto.recrutamento.infra.repositorys.templatevaga;

import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TemplateVagaJpaRepository extends JpaRepository<TemplateVaga, UUID>, JpaSpecificationExecutor<TemplateVaga> {
    Optional<TemplateVaga> findByIdAndExcluidoEmIsNull(UUID id);
}
