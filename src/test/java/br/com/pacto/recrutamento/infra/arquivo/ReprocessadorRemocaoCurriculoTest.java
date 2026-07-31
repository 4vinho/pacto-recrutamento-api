package br.com.pacto.recrutamento.infra.arquivo;

import br.com.pacto.recrutamento.app.ports.curriculo.ArquivoStorage;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

class ReprocessadorRemocaoCurriculoTest {
    private static final OffsetDateTime AGORA = OffsetDateTime.parse("2026-07-30T12:00:00Z");
    private final ArquivoStorage storage = mock(ArquivoStorage.class);
    private final RemocaoCurriculoPendenteJpaAdapter pendencias =
            mock(RemocaoCurriculoPendenteJpaAdapter.class);
    private final ReprocessadorRemocaoCurriculo reprocessador =
            new ReprocessadorRemocaoCurriculo(storage, pendencias,
                    Clock.fixed(Instant.parse("2026-07-30T12:00:00Z"), ZoneOffset.UTC));

    @Test
    void concluiPendenciaSomenteDepoisDeRemoverObjeto() {
        RemocaoArquivoPendente pendencia = pendencia();
        when(pendencias.buscarPendentes(50)).thenReturn(Collections.singletonList(pendencia));

        reprocessador.reprocessar();

        verify(storage).remover(pendencia.getStorageKey());
        verify(pendencias).concluir(pendencia.getId());
    }

    @Test
    void mantemPendenciaERegistraTentativaComMotivoSanitizado() {
        RemocaoArquivoPendente pendencia = pendencia();
        when(pendencias.buscarPendentes(50)).thenReturn(Collections.singletonList(pendencia));
        doThrow(new RuntimeException("falha\r\ncom detalhes"))
                .when(storage).remover(pendencia.getStorageKey());

        reprocessador.reprocessar();

        verify(pendencias).registrarFalha(
                eq(pendencia.getId()),
                argThat(motivo -> !motivo.contains("\r") && !motivo.contains("\n")
                        && motivo.length() <= 500),
                eq(AGORA));
    }

    @Test
    void pendenciaIncrementaTentativasESanitizaMotivo() {
        RemocaoArquivoPendente pendencia = pendencia();

        pendencia.registrarFalha("erro\r\ninterno", AGORA.plusMinutes(1));

        assertThat(pendencia.getTentativas()).isEqualTo(1);
        assertThat(pendencia.getMotivo()).isEqualTo("erro  interno");
        assertThat(pendencia.getUltimaTentativaEm()).isEqualTo(AGORA.plusMinutes(1));
    }

    private RemocaoArquivoPendente pendencia() {
        return new RemocaoArquivoPendente(
                UUID.randomUUID(), "curriculos/arquivo.pdf", "falha inicial", 0, AGORA);
    }
}
