package br.com.pacto.recrutamento.infra.repositorys.templatevaga;

import br.com.pacto.recrutamento.core.entities.RequisitoTemplateVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequisitoTemplateVagaJpaRepository extends JpaRepository<RequisitoTemplateVaga, UUID> {
    Optional<RequisitoTemplateVaga> findByIdAndExcluidoEmIsNull(UUID id);

    List<RequisitoTemplateVaga> findByTemplateVagaIdAndExcluidoEmIsNull(UUID templateId);
}
