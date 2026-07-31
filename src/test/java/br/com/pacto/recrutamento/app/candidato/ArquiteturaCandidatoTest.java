package br.com.pacto.recrutamento.app.ports.candidato;

import br.com.pacto.recrutamento.core.entities.Candidato;
import br.com.pacto.recrutamento.infra.candidato.CandidatoJpaAdapter;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import static org.assertj.core.api.Assertions.assertThat;

class ArquiteturaCandidatoTest {

    @Test
    void aplicacaoDefinePortaSemConhecerJpaEAdapterFicaNaInfraestrutura() {
        assertThat(CandidatoRepository.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.app.ports.candidato");
        assertThat(CandidatoRepository.class.isAnnotationPresent(Entity.class)).isFalse();
        assertThat(CandidatoJpaAdapter.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.infra.candidato");
        assertThat(Candidato.class).hasAnnotation(Entity.class);
    }

    @Test
    void repositoryPersisteDiretamenteAEntidadeDoCore() throws NoSuchMethodException {
        assertThat(CandidatoRepository.class.getMethod("salvar", Candidato.class).getReturnType())
                .isEqualTo(Candidato.class);
    }
}
