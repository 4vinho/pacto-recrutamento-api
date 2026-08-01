package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.CandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.RespostaCandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.RespostaRequisitoCandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.HistoricoCandidaturaJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidaturaJpaAdapterTest {
    private final CandidaturaJpaRepository candidaturas = mock(CandidaturaJpaRepository.class);
    private final RespostaCandidaturaJpaRepository respostas = mock(RespostaCandidaturaJpaRepository.class);
    private final RespostaRequisitoCandidaturaJpaRepository respostasRequisitos =
            mock(RespostaRequisitoCandidaturaJpaRepository.class);
    private final CandidaturaJpaAdapter adapter = new CandidaturaJpaAdapter(
            candidaturas, respostas, respostasRequisitos, mock(HistoricoCandidaturaJpaRepository.class));

    @Test
    void traduzUnicidadeConcorrenteDaCandidatura() {
        when(candidaturas.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("uk"));
        assertThrows(CandidaturaPort.CandidaturaDuplicadaException.class,
                () -> adapter.salvar(new Candidatura(java.util.UUID.randomUUID(), java.util.UUID.randomUUID())));
    }

    @Test
    void traduzUnicidadeDeRespostaNoLoteAtomico() {
        when(respostas.saveAllAndFlush(any())).thenThrow(new DataIntegrityViolationException("uk"));
        RespostaCandidatura resposta = new RespostaCandidatura(java.util.UUID.randomUUID(),
                java.util.UUID.randomUUID(), "valor");
        assertThrows(CandidaturaPort.RespostasDuplicadasException.class,
                () -> adapter.registrarRespostasPerguntasAtomicamente(
                        new Candidatura(java.util.UUID.randomUUID(), java.util.UUID.randomUUID()),
                        Collections.singletonList(resposta)));
    }

    @Test
    void atualizacaoDoRascunhoUsaBloqueioPessimista() throws Exception {
        Lock lock = CandidaturaJpaRepository.class.getMethod("findByIdForUpdate",
                java.util.UUID.class).getAnnotation(Lock.class);

        assertNotNull(lock);
        assertEquals(LockModeType.PESSIMISTIC_WRITE, lock.value());
    }
}
