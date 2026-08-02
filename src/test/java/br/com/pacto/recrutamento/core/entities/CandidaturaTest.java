package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class CandidaturaTest {
    @Test
    void somenteEnviaDepoisDoCurriculoEDasEtapas() {
        UUID usuarioId = UUID.randomUUID();
        Candidatura candidatura = new Candidatura(usuarioId, UUID.randomUUID());

        candidatura.configurarEtapas(true, true);
        candidatura.registrarPerguntasRespondidas();
        candidatura.registrarRequisitosRespondidos();

        assertThat(candidatura.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(candidatura.getStatus()).isEqualTo(StatusCandidatura.RASCUNHO);

        candidatura.registrarCurriculoEnviado();

        assertThat(candidatura.isCurriculoEnviado()).isTrue();
        assertThat(candidatura.getStatus()).isEqualTo(StatusCandidatura.ENVIADA);
    }

    @Test
    void curriculoConcluiCandidaturaSemPerguntasNemRequisitos() {
        Candidatura candidatura = new Candidatura(UUID.randomUUID(), UUID.randomUUID());
        candidatura.configurarEtapas(false, false);

        candidatura.registrarCurriculoEnviado();

        assertThat(candidatura.getStatus()).isEqualTo(StatusCandidatura.ENVIADA);
    }

    @Test
    void permitePercorrerEtapasEReabrirUmaDecisao() {
        Candidatura candidatura = new Candidatura(UUID.randomUUID(), UUID.randomUUID());
        candidatura.setStatus(StatusCandidatura.ENVIADA);
        candidatura.setStatus(StatusCandidatura.TRIAGEM);
        candidatura.setStatus(StatusCandidatura.ENTREVISTA_COMPORTAMENTAL);
        candidatura.setStatus(StatusCandidatura.ENTREVISTA_TECNICA);
        candidatura.setStatus(StatusCandidatura.APROVADA);

        candidatura.setStatus(StatusCandidatura.TRIAGEM);
        candidatura.setStatus(StatusCandidatura.REJEITADA);
        candidatura.setStatus(StatusCandidatura.TRIAGEM);

        assertThat(candidatura.getStatus()).isEqualTo(StatusCandidatura.TRIAGEM);
    }
}
