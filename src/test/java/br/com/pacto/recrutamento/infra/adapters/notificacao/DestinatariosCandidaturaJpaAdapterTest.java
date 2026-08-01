package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.infra.projections.DestinatariosCandidaturaProjection;
import br.com.pacto.recrutamento.infra.repositorys.notificacao.DestinatariosCandidaturaRepository;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class DestinatariosCandidaturaJpaAdapterTest {
    @Test
    void projecaoProduzResponsaveisEUsuario() {
        UUID responsavel = UUID.randomUUID();
        UUID outroResponsavel = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();
        DestinatariosCandidaturaProjection primeira = projecao(responsavel, usuario);
        DestinatariosCandidaturaProjection segunda = projecao(outroResponsavel, usuario);
        DestinatariosCandidaturaRepository repository = id -> Arrays.asList(primeira, segunda);

        assertThat(new DestinatariosCandidaturaJpaAdapter(repository).buscarPorCandidatura(UUID.randomUUID()))
                .hasValueSatisfying(destinatarios -> {
                    assertThat(destinatarios.getResponsaveisIds())
                            .containsExactly(responsavel, outroResponsavel);
                    assertThat(destinatarios.getUsuarioId()).isEqualTo(usuario);
                });
    }

    private DestinatariosCandidaturaProjection projecao(UUID responsavel, UUID usuario) {
        return new DestinatariosCandidaturaProjection() {
            public UUID getResponsavelId() { return responsavel; }
            public UUID getUsuarioId() { return usuario; }
        };
    }
}
