package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.infra.projections.DestinatariosCandidaturaProjection;
import br.com.pacto.recrutamento.infra.repositorys.notificacao.DestinatariosCandidaturaRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DestinatariosCandidaturaJpaAdapterTest {
    @Test
    void projecaoDeCandidaturaProduzResponsavelECandidato() {
        UUID responsavel = UUID.randomUUID();
        UUID outroResponsavel = UUID.randomUUID();
        UUID candidato = UUID.randomUUID();
        DestinatariosCandidaturaProjection primeira = new DestinatariosCandidaturaProjection() {
            public UUID getResponsavelId() {
                return responsavel;
            }

            public UUID getCandidatoId() {
                return candidato;
            }
        };
        DestinatariosCandidaturaProjection segunda = new DestinatariosCandidaturaProjection() {
            public UUID getResponsavelId() {
                return outroResponsavel;
            }

            public UUID getCandidatoId() {
                return candidato;
            }
        };
        DestinatariosCandidaturaRepository repository = candidaturaId -> Arrays.asList(primeira, segunda);

        assertThat(new DestinatariosCandidaturaJpaAdapter(repository).buscarPorCandidatura(UUID.randomUUID()))
                .hasValueSatisfying(destinatarios -> {
                    assertThat(destinatarios.getResponsaveisIds())
                            .containsExactly(responsavel, outroResponsavel);
                    assertThat(destinatarios.getCandidatoId()).isEqualTo(candidato);
                });
    }
}
