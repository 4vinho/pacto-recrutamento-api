package br.com.pacto.recrutamento.infra.templatevaga;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface PerguntaTemplateVagaJpaRepository extends JpaRepository<PerguntaTemplateVagaJpaEntity, UUID> {
    Optional<PerguntaTemplateVagaJpaEntity> findByIdAndExcluidoEmIsNull(UUID id);
    List<PerguntaTemplateVagaJpaEntity> findByTemplateVagaIdAndExcluidoEmIsNull(UUID templateId);
}
