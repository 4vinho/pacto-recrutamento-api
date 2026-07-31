package br.com.pacto.recrutamento.infra.repositorys.templatevaga;

import br.com.pacto.recrutamento.core.entities.PerguntaTemplateVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerguntaTemplateVagaJpaRepository extends JpaRepository<PerguntaTemplateVaga, UUID> {
    Optional<PerguntaTemplateVaga> findByIdAndExcluidoEmIsNull(UUID id);

    List<PerguntaTemplateVaga> findByTemplateVagaIdAndExcluidoEmIsNull(UUID templateId);
}
