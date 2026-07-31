package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import org.springframework.stereotype.Component;

@Component
public class RequisitoVagaJpaMapper {
    RequisitoVagaJpaEntity paraEntidade(RequisitoVaga requisito) {
        return new RequisitoVagaJpaEntity(requisito.getId(), requisito.getVagaId(),
                requisito.getDescricao(), requisito.isObrigatorio(), requisito.getCriadoEm(),
                requisito.getAtualizadoEm(), requisito.getExcluidoEm());
    }

    RequisitoVaga paraDominio(RequisitoVagaJpaEntity entity) {
        RequisitoVaga requisito = new RequisitoVaga();
        requisito.setId(entity.getId());
        requisito.setVagaId(entity.getVagaId());
        requisito.setDescricao(entity.getDescricao());
        requisito.setObrigatorio(entity.isObrigatorio());
        requisito.setCriadoEm(entity.getCriadoEm());
        requisito.setAtualizadoEm(entity.getAtualizadoEm());
        requisito.setExcluidoEm(entity.getExcluidoEm());
        return requisito;
    }
}
