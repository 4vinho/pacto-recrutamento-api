package br.com.pacto.recrutamento.app.candidato;

import br.com.pacto.recrutamento.infra.candidato.CandidatoJpaAdapter;
import br.com.pacto.recrutamento.infra.candidato.CandidatoJpaEntity;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import static org.assertj.core.api.Assertions.assertThat;

class ArquiteturaCandidatoTest {

    @Test
    void aplicacaoDefinePortaSemConhecerJpaEAdapterFicaNaInfraestrutura() {
        assertThat(CandidatoRepository.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.app.candidato");
        assertThat(CandidatoRepository.class.isAnnotationPresent(Entity.class)).isFalse();
        assertThat(CandidatoJpaAdapter.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.infra.candidato");
        assertThat(CandidatoJpaEntity.class).hasAnnotation(Entity.class);
    }
}
