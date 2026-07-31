package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.infra.projections.DestinatariosCandidaturaProjection;
import br.com.pacto.recrutamento.infra.repositorys.notificacao.DestinatariosCandidaturaRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DestinatariosCandidaturaJpaAdapterTest {
    @Test
    void projecaoDeCandidaturaProduzResponsavelECandidato() {
        UUID responsavel = UUID.randomUUID();
        UUID candidato = UUID.randomUUID();
        DestinatariosCandidaturaRepository repository = candidaturaId -> Optional.of(new DestinatariosCandidaturaProjection() {
            public UUID getResponsavelId() {
                return responsavel;
            }

            public UUID getCandidatoId() {
                return candidato;
            }
        });

        assertThat(new DestinatariosCandidaturaJpaAdapter(repository).buscarPorCandidatura(UUID.randomUUID()))
                .hasValueSatisfying(destinatarios -> {
                    assertThat(destinatarios.getResponsavelId()).isEqualTo(responsavel);
                    assertThat(destinatarios.getCandidatoId()).isEqualTo(candidato);
                });
    }
}
