package br.com.pacto.recrutamento.app.ports.out.candidato;

import br.com.pacto.recrutamento.core.entities.Candidato;
import br.com.pacto.recrutamento.infra.adapters.candidato.CandidatoJpaAdapter;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import static org.assertj.core.api.Assertions.assertThat;

class ArquiteturaCandidatoTest {

    @Test
    void aplicacaoDefinePortaSemConhecerJpaEAdapterFicaNaInfraestrutura() {
        assertThat(CandidatoPort.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.app.ports.out.candidato");
        assertThat(CandidatoPort.class.isAnnotationPresent(Entity.class)).isFalse();
        assertThat(CandidatoJpaAdapter.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.infra.adapters.candidato");
        assertThat(Candidato.class).hasAnnotation(Entity.class);
    }

    @Test
    void repositoryPersisteDiretamenteAEntidadeDoCore() throws NoSuchMethodException {
        assertThat(CandidatoPort.class.getMethod("salvar", Candidato.class).getReturnType())
                .isEqualTo(Candidato.class);
    }
}
