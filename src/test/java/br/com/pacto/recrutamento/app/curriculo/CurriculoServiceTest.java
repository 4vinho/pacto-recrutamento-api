package br.com.pacto.recrutamento.app.curriculo;

import br.com.pacto.recrutamento.app.dtos.curriculo.EnviarCurriculoDTO;
import br.com.pacto.recrutamento.app.ports.out.candidatura.AutorizacaoResponsavelCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.EventosCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.curriculo.ArquivoStoragePort;
import br.com.pacto.recrutamento.app.ports.out.curriculo.CurriculoPort;
import br.com.pacto.recrutamento.app.usecases.curriculo.CurriculoService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurriculoServiceTest {
    private final CurriculoPort curriculos = mock(CurriculoPort.class);
    private final ArquivoStoragePort storage = mock(ArquivoStoragePort.class);
    private final CandidaturaPort candidaturas = mock(CandidaturaPort.class);
    private final AutorizacaoResponsavelCandidaturaPort autorizacao =
            mock(AutorizacaoResponsavelCandidaturaPort.class);
    private final EventosCandidaturaPort eventos = mock(EventosCandidaturaPort.class);
    private final CurriculoService service = new CurriculoService(
            curriculos, storage, candidaturas, autorizacao, eventos, Clock.systemUTC());

    @Test
    void envioDeCurriculoPublicaEventoQuandoCandidaturaFicaCompleta() {
        UUID usuarioId = UUID.randomUUID();
        UUID vagaId = UUID.randomUUID();
        Candidatura candidatura = new Candidatura(usuarioId, vagaId);
        candidatura.setPerguntasRespondidas(true);
        candidatura.setRequisitosRespondidos(true);

        when(candidaturas.buscarPorIdParaAtualizacao(candidatura.getId()))
                .thenReturn(Optional.of(candidatura));
        when(curriculos.buscarAtivoPorCandidatura(candidatura.getId()))
                .thenReturn(Optional.empty());

        TypedResponse<?> response = service.enviarCurriculo(new EnviarCurriculoDTO(
                usuarioId, candidatura.getId(), "curriculo.pdf", "application/pdf",
                "%PDF-1.4".getBytes()));

        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(candidatura.getStatus()).isEqualTo(StatusCandidatura.ENVIADA);
        verify(candidaturas).salvar(candidatura);
        verify(eventos).candidaturaCriada(candidatura);
    }
}
