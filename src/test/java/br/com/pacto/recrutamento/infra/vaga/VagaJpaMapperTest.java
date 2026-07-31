package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.enums.StatusVaga;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VagaJpaMapperTest {
    @Test
    void preservaIdentidadeStatusTerminalEAuditoriaNoRoundTrip() {
        OffsetDateTime criadaEm = OffsetDateTime.parse("2026-01-01T10:00:00Z");
        OffsetDateTime atualizadaEm = criadaEm.plusDays(1);
        Vaga vaga = Vaga.restaurar(UUID.randomUUID(), UUID.randomUUID(), "Java", "Descricao",
                StatusVaga.ENCERRADA, criadaEm, atualizadaEm, null);
        VagaJpaMapper mapper = new VagaJpaMapper();

        Vaga restaurada = mapper.paraDominio(mapper.paraEntidade(vaga));

        assertThat(restaurada.getId()).isEqualTo(vaga.getId());
        assertThat(restaurada.getStatus()).isEqualTo(StatusVaga.ENCERRADA);
        assertThat(restaurada.getCriadoEm()).isEqualTo(criadaEm);
        assertThat(restaurada.getAtualizadoEm()).isEqualTo(atualizadaEm);
    }
}
