package br.com.pacto.recrutamento.infra.vaga;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class VagaJpaAdapterTest {
    @Test
    void buscaPorIdUsaConsultaQueIgnoraExcluidosLogicamente() {
        UUID id = UUID.randomUUID();
        VagaJpaRepository repository = mock(VagaJpaRepository.class);
        when(repository.findByIdAndExcluidoEmIsNull(id)).thenReturn(Optional.empty());
        VagaJpaAdapter adapter = new VagaJpaAdapter(repository);

        assertThat(adapter.buscarAtivaPorId(id)).isEmpty();

        verify(repository).findByIdAndExcluidoEmIsNull(id);
    }

    @Test
    void buscasDosFilhosTambemIgnoramExcluidosLogicamente() {
        UUID id = UUID.randomUUID();
        PerguntaVagaJpaRepository perguntas = mock(PerguntaVagaJpaRepository.class);
        RequisitoVagaJpaRepository requisitos = mock(RequisitoVagaJpaRepository.class);
        when(perguntas.findByIdAndExcluidoEmIsNull(id)).thenReturn(Optional.empty());
        when(requisitos.findByIdAndExcluidoEmIsNull(id)).thenReturn(Optional.empty());

        assertThat(new PerguntaVagaJpaAdapter(perguntas)
                .buscarAtivaPorId(id)).isEmpty();
        assertThat(new RequisitoVagaJpaAdapter(requisitos)
                .buscarAtivoPorId(id)).isEmpty();

        verify(perguntas).findByIdAndExcluidoEmIsNull(id);
        verify(requisitos).findByIdAndExcluidoEmIsNull(id);
    }
}
