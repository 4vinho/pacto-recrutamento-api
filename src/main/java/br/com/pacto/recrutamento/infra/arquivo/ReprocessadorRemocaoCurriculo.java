package br.com.pacto.recrutamento.infra.arquivo;

import br.com.pacto.recrutamento.app.curriculo.ArquivoStorage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Component
public class ReprocessadorRemocaoCurriculo {
    private static final int TAMANHO_LOTE = 50;
    private final ArquivoStorage storage;
    private final RemocaoCurriculoPendenteJpaAdapter pendencias;
    private final Clock clock;

    public ReprocessadorRemocaoCurriculo(
            ArquivoStorage storage,
            RemocaoCurriculoPendenteJpaAdapter pendencias,
            Clock clock) {
        this.storage = storage;
        this.pendencias = pendencias;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${storage.minio.reprocessamento-ms:60000}")
    public void reprocessar() {
        for (RemocaoArquivoPendente pendencia : pendencias.buscarPendentes(TAMANHO_LOTE)) {
            reprocessar(pendencia);
        }
    }

    private void reprocessar(RemocaoArquivoPendente pendencia) {
        try {
            storage.remover(pendencia.getStorageKey());
            pendencias.concluir(pendencia.getId());
        } catch (RuntimeException e) {
            pendencias.registrarFalha(
                    pendencia.getId(), MotivoRemocao.sanitizar(e.getMessage()),
                    OffsetDateTime.now(clock));
        }
    }
}
