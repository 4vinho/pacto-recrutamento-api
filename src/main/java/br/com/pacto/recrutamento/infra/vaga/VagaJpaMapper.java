package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.stereotype.Component;

@Component
public class VagaJpaMapper {
    VagaJpaEntity paraEntidade(Vaga vaga) {
        return new VagaJpaEntity(vaga.getId(), vaga.getResponsavelId(), vaga.getTitulo(),
                vaga.getDescricao(), vaga.getStatus(), vaga.getCriadoEm(),
                vaga.getAtualizadoEm(), vaga.getExcluidoEm());
    }

    Vaga paraDominio(VagaJpaEntity entity) {
        return Vaga.restaurar(entity.getId(), entity.getResponsavelId(), entity.getTitulo(),
                entity.getDescricao(), entity.getStatus(), entity.getCriadoEm(),
                entity.getAtualizadoEm(), entity.getExcluidoEm());
    }
}
