package br.com.pacto.recrutamento.infra.templatevaga;

import br.com.pacto.recrutamento.core.entities.RequisitoTemplateVaga;
import org.springframework.stereotype.Component;

@Component
class RequisitoTemplateVagaJpaMapper {
    RequisitoTemplateVagaJpaEntity paraEntidade(RequisitoTemplateVaga requisito) {
        return new RequisitoTemplateVagaJpaEntity(requisito.getId(), requisito.getTemplateVagaId(),
                requisito.getDescricao(), requisito.isObrigatorio(), requisito.getCriadoEm(),
                requisito.getAtualizadoEm(), requisito.getExcluidoEm());
    }

    RequisitoTemplateVaga paraDominio(RequisitoTemplateVagaJpaEntity entity) {
        RequisitoTemplateVaga requisito = new RequisitoTemplateVaga();
        requisito.setId(entity.getId());
        requisito.setTemplateVagaId(entity.getTemplateVagaId());
        requisito.setDescricao(entity.getDescricao());
        requisito.setObrigatorio(entity.isObrigatorio());
        requisito.setCriadoEm(entity.getCriadoEm());
        requisito.setAtualizadoEm(entity.getAtualizadoEm());
        requisito.setExcluidoEm(entity.getExcluidoEm());
        return requisito;
    }
}
