package br.com.pacto.recrutamento.app.ports.out.curriculo;

import br.com.pacto.recrutamento.app.dtos.curriculo.EnviarCurriculoDTO;
import br.com.pacto.recrutamento.app.dtos.curriculo.GerarUrlTemporariaCurriculoDTO;
import br.com.pacto.recrutamento.app.dtos.curriculo.SubstituirCurriculoDTO;
import br.com.pacto.recrutamento.app.serviceImpl.CurriculoServiceImpl;
import br.com.pacto.recrutamento.core.entities.Curriculo;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CurriculoServiceImplTest {
    private static final byte[] PDF = "%PDF-1.7\nconteudo".getBytes();
    private static final UUID USUARIO = UUID.randomUUID();
    private static final UUID CANDIDATO = UUID.randomUUID();
    private final CurriculoPort repositorio = mock(CurriculoPort.class);
    private final ArquivoStoragePort storage = mock(ArquivoStoragePort.class);
    private final CandidatoConsultaPort candidatos = mock(CandidatoConsultaPort.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-07-30T12:00:00Z"), ZoneOffset.UTC);
    private final CurriculoServiceImpl service = new CurriculoServiceImpl(
            repositorio, storage, candidatos, clock);

    @Test
    void enviarRecusaArquivoSemAssinaturaPdfReal() {
        assertThat(service.enviarCurriculo(new EnviarCurriculoDTO(
                USUARIO, "curriculo.pdf", "application/pdf", "texto".getBytes())).getStatusCode())
                .isEqualTo(400);

        verify(storage, never()).armazenar(any(String.class), any(byte[].class), any(String.class));
    }

    @Test
    void enviarRecusaArquivoMaiorQueCincoMegabytes() {
        byte[] maiorQueLimite = new byte[5 * 1024 * 1024 + 1];
        System.arraycopy(PDF, 0, maiorQueLimite, 0, PDF.length);

        assertThat(service.enviarCurriculo(new EnviarCurriculoDTO(
                USUARIO, "curriculo.pdf", "application/pdf", maiorQueLimite)).getStatusCode())
                .isEqualTo(400);
    }

    @Test
    void enviarArmazenaMetadadosComChecksumSha256() {
        when(candidatos.buscarIdPorUsuario(USUARIO)).thenReturn(Optional.of(CANDIDATO));
        when(repositorio.buscarAtivoPorCandidato(CANDIDATO)).thenReturn(Optional.<Curriculo>empty());

        assertThat(service.enviarCurriculo(new EnviarCurriculoDTO(
                USUARIO, "curriculo.pdf", "text/plain", PDF)).getStatusCode()).isEqualTo(201);

        verify(storage).armazenar(any(String.class), eq(PDF), eq("application/pdf"));
        verify(repositorio).salvar(any(Curriculo.class));
        org.mockito.ArgumentCaptor<Curriculo> captor = org.mockito.ArgumentCaptor.forClass(Curriculo.class);
        verify(repositorio).salvar(captor.capture());
        assertThat(captor.getValue().getChecksumSha256())
                .isEqualTo("4d17fbec8ebedac1a890c3c494389fbd000cd0393a1f09068686a297c8551701");
    }

    @Test
    void enviarCompensaObjetoQuandoMetadadosFalham() {
        when(candidatos.buscarIdPorUsuario(USUARIO)).thenReturn(Optional.of(CANDIDATO));
        when(repositorio.buscarAtivoPorCandidato(CANDIDATO)).thenReturn(Optional.<Curriculo>empty());
        doThrow(new RuntimeException("banco indisponivel")).when(repositorio).salvar(any(Curriculo.class));

        assertThat(service.enviarCurriculo(new EnviarCurriculoDTO(
                USUARIO, "curriculo.pdf", "application/pdf", PDF)).getStatusCode()).isEqualTo(500);

        verify(storage).remover(any(String.class));
    }

    @Test
    void enviarRetornaErroDoBancoQuandoCompensacaoTambemFalha() {
        when(candidatos.buscarIdPorUsuario(USUARIO)).thenReturn(Optional.of(CANDIDATO));
        when(repositorio.buscarAtivoPorCandidato(CANDIDATO)).thenReturn(Optional.<Curriculo>empty());
        doThrow(new RuntimeException("banco indisponivel")).when(repositorio).salvar(any(Curriculo.class));
        doThrow(new RuntimeException("minio indisponivel")).when(storage).remover(any(String.class));

        assertThat(service.enviarCurriculo(new EnviarCurriculoDTO(
                USUARIO, "curriculo.pdf", "application/pdf", PDF)).getStatusCode()).isEqualTo(500);

        verify(storage).remover(any(String.class));
    }

    @Test
    void substituirMantemAnteriorAtivoQuandoNovoArquivoNaoPodeSerSalvo() {
        Curriculo anterior = curriculo(CANDIDATO);
        when(candidatos.buscarIdPorUsuario(USUARIO)).thenReturn(Optional.of(CANDIDATO));
        when(repositorio.buscarAtivoPorCandidato(CANDIDATO)).thenReturn(Optional.of(anterior));
        doThrow(new RuntimeException("storage indisponivel")).when(storage)
                .armazenar(any(String.class), any(byte[].class), any(String.class));

        assertThat(service.substituirCurriculo(new SubstituirCurriculoDTO(
                USUARIO, "novo.pdf", "application/pdf", PDF)).getStatusCode()).isEqualTo(500);

        verify(repositorio, never()).substituir(any(Curriculo.class), any(Curriculo.class), any(OffsetDateTime.class));
    }

    @Test
    void substituirMantemSucessoQuandoObjetoAntigoNaoPodeSerRemovido() {
        Curriculo anterior = curriculo(CANDIDATO);
        when(candidatos.buscarIdPorUsuario(USUARIO)).thenReturn(Optional.of(CANDIDATO));
        when(repositorio.buscarAtivoPorCandidato(CANDIDATO)).thenReturn(Optional.of(anterior));
        doThrow(new RuntimeException("falha remocao")).when(storage).remover(anterior.getStorageKey());

        assertThat(service.substituirCurriculo(new SubstituirCurriculoDTO(
                USUARIO, "novo.pdf", "application/pdf", PDF)).getStatusCode()).isEqualTo(200);

        verify(repositorio).substituir(eq(anterior), any(Curriculo.class), any(OffsetDateTime.class));
    }

    @Test
    void gerarUrlRecusaSolicitanteQueNaoPossuiCurriculo() {
        Curriculo curriculo = curriculo(CANDIDATO);
        when(repositorio.buscarAtivoPorId(curriculo.getId())).thenReturn(Optional.of(curriculo));
        when(candidatos.pertenceAoUsuario(CANDIDATO, USUARIO)).thenReturn(false);

        assertThat(service.gerarUrlTemporariaCurriculo(
                new GerarUrlTemporariaCurriculoDTO(USUARIO, curriculo.getId())).getStatusCode()).isEqualTo(403);

        verify(storage, never()).gerarUrlTemporaria(any(String.class), any(Duration.class));
    }

    @Test
    void gerarUrlPrivadaComExpiracaoExataDeCincoMinutos() {
        Curriculo curriculo = curriculo(CANDIDATO);
        when(repositorio.buscarAtivoPorId(curriculo.getId())).thenReturn(Optional.of(curriculo));
        when(candidatos.pertenceAoUsuario(CANDIDATO, USUARIO)).thenReturn(true);
        when(storage.gerarUrlTemporaria(curriculo.getStorageKey(), Duration.ofMinutes(5)))
                .thenReturn("https://privado/assinada");

        br.com.pacto.recrutamento.core.common.TypedResponse<br.com.pacto.recrutamento.app.dtos.curriculo.UrlTemporariaCurriculoDTO> resposta =
                service.gerarUrlTemporariaCurriculo(new GerarUrlTemporariaCurriculoDTO(USUARIO, curriculo.getId()));

        assertThat(resposta.getStatusCode()).isEqualTo(200);
        assertThat(resposta.getData().getExpiraEm()).isEqualTo(OffsetDateTime.parse("2026-07-30T12:05:00Z"));
        verify(storage).gerarUrlTemporaria(curriculo.getStorageKey(), Duration.ofMinutes(5));
    }

    private Curriculo curriculo(UUID candidatoId) {
        return new Curriculo(candidatoId, "curriculos/anterior.pdf", "anterior.pdf",
                "application/pdf", PDF.length, new String(new char[64]).replace('\0', 'a'));
    }
}
