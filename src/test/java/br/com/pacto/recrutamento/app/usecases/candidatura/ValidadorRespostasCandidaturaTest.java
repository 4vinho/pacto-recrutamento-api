package br.com.pacto.recrutamento.app.usecases.candidatura;

import br.com.pacto.recrutamento.app.dtos.candidatura.RespostaCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.RespostaRequisitoCandidaturaDTO;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;
import br.com.pacto.recrutamento.core.enums.TipoResposta;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ValidadorRespostasCandidaturaTest {
    private final Candidatura candidatura = new Candidatura(UUID.randomUUID(), UUID.randomUUID());

    @Test
    void aceitaRespostaCompativelComAPergunta() {
        PerguntaVaga pergunta = pergunta(TipoResposta.NUMERO, true);

        assertThat(ValidadorRespostasCandidatura.validarPerguntas(candidatura,
                Collections.singletonList(new RespostaCandidaturaDTO(pergunta.getId(), " 12 ")),
                Collections.singletonList(pergunta))).isPresent();
    }

    @Test
    void rejeitaPerguntaDuplicadaOuValorIncompativel() {
        PerguntaVaga pergunta = pergunta(TipoResposta.NUMERO, true);
        RespostaCandidaturaDTO resposta = new RespostaCandidaturaDTO(pergunta.getId(), "12");

        assertThat(ValidadorRespostasCandidatura.validarPerguntas(candidatura,
                Arrays.asList(resposta, resposta), Collections.singletonList(pergunta))).isEmpty();
        assertThat(ValidadorRespostasCandidatura.validarPerguntas(candidatura,
                Collections.singletonList(new RespostaCandidaturaDTO(pergunta.getId(), "texto")),
                Collections.singletonList(pergunta))).isEmpty();
    }

    @Test
    void rejeitaPerguntaObrigatoriaSemResposta() {
        PerguntaVaga pergunta = pergunta(TipoResposta.TEXTO, true);

        assertThat(ValidadorRespostasCandidatura.validarPerguntas(candidatura,
                Collections.emptyList(), Collections.singletonList(pergunta))).isEmpty();
    }

    @Test
    void validaPertencimentoEObrigatoriedadeDoRequisito() {
        RequisitoVaga requisito = new RequisitoVaga();
        requisito.setObrigatorio(true);
        RespostaRequisitoCandidaturaDTO resposta = new RespostaRequisitoCandidaturaDTO(
                requisito.getId(), NivelAtendimentoRequisito.ALTO);

        assertThat(ValidadorRespostasCandidatura.validarRequisitos(candidatura,
                Collections.singletonList(resposta), Collections.singletonList(requisito))).isPresent();
        assertThat(ValidadorRespostasCandidatura.validarRequisitos(candidatura,
                Collections.emptyList(), Collections.singletonList(requisito))).isEmpty();
    }

    private PerguntaVaga pergunta(TipoResposta tipo, boolean obrigatoria) {
        PerguntaVaga pergunta = new PerguntaVaga();
        pergunta.setTipoResposta(tipo);
        pergunta.setObrigatoria(obrigatoria);
        return pergunta;
    }
}
