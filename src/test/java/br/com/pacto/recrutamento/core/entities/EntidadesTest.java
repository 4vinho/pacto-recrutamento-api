package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import br.com.pacto.recrutamento.core.enums.StatusVaga;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class EntidadesTest {

    @Test
    void novaCandidaturaComecaComoEnviada() {
        Candidatura candidatura = new Candidatura(UUID.randomUUID(), UUID.randomUUID());

        assertThat(candidatura.getStatus()).isEqualTo(StatusCandidatura.RASCUNHO);
    }

    @Test
    void novaVagaComecaComoRascunho() {
        Vaga vaga = new Vaga(UUID.randomUUID(), "Desenvolvedor", "Descrição da vaga");

        assertThat(vaga.getStatus()).isEqualTo(StatusVaga.RASCUNHO);
        assertThat(vaga.aceitaCandidatura()).isFalse();
    }

    @Test
    void refreshTokenRevogadoNaoPodeCriarSessao() {
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                "hash",
                UUID.randomUUID(),
                OffsetDateTime.now().plusHours(1)
        );

        token.revogar(OffsetDateTime.now());

        assertThat(token.podeCriarSessao(OffsetDateTime.now())).isFalse();
    }

    @Test
    void vagaEncerradaNaoPodeSerPublicadaNovamente() {
        Vaga vaga = new Vaga(UUID.randomUUID(), "Desenvolvedor", "Descrição");
        vaga.setStatus(StatusVaga.PUBLICADA);
        vaga.setStatus(StatusVaga.ENCERRADA);

        assertThatIllegalStateException()
                .isThrownBy(() -> vaga.setStatus(StatusVaga.PUBLICADA));
    }

    @Test
    void candidaturaAprovadaPodeVoltarParaAnalise() {
        Candidatura candidatura = new Candidatura(UUID.randomUUID(), UUID.randomUUID());
        candidatura.setStatus(StatusCandidatura.ENVIADA);
        candidatura.setStatus(StatusCandidatura.TRIAGEM);
        candidatura.setStatus(StatusCandidatura.APROVADA);

        candidatura.setStatus(StatusCandidatura.TRIAGEM);

        assertThat(candidatura.getStatus()).isEqualTo(StatusCandidatura.TRIAGEM);
    }

    @Test
    void perguntaRejeitaOrdemNaoPositiva() {
        PerguntaVaga pergunta = new PerguntaVaga();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> pergunta.setOrdem(0));
    }

    @Test
    void curriculoRejeitaArquivoMaiorQueCincoMegabytes() {
        Curriculo curriculo = new Curriculo();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> curriculo.setTamanhoBytes(5L * 1024 * 1024 + 1));
    }
}
