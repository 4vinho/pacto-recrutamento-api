package br.com.pacto.recrutamento.infra.templatevaga;

import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import org.springframework.stereotype.Component;

@Component
class TemplateVagaJpaMapper {
    TemplateVagaJpaEntity paraEntidade(TemplateVaga template) {
        return new TemplateVagaJpaEntity(template.getId(), template.getResponsavelId(),
                template.getTitulo(), template.getDescricao(), template.getCriadoEm(),
                template.getAtualizadoEm(), template.getExcluidoEm());
    }

    TemplateVaga paraDominio(TemplateVagaJpaEntity entity) {
        TemplateVaga template = new TemplateVaga(entity.getResponsavelId(), entity.getTitulo(), entity.getDescricao());
        template.setId(entity.getId());
        template.setCriadoEm(entity.getCriadoEm());
        template.setAtualizadoEm(entity.getAtualizadoEm());
        template.setExcluidoEm(entity.getExcluidoEm());
        return template;
    }
}
