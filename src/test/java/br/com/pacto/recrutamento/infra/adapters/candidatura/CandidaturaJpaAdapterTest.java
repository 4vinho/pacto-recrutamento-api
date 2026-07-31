package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.infra.repositorys.candidatura.RespostaCandidaturaJpaRepository;

import br.com.pacto.recrutamento.infra.repositorys.candidatura.CandidaturaJpaRepository;

import br.com.pacto.recrutamento.app.ports.candidatura.CandidaturaRepositorio;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidaturaJpaAdapterTest {
    private final CandidaturaJpaRepository candidaturas = mock(CandidaturaJpaRepository.class);
    private final RespostaCandidaturaJpaRepository respostas = mock(RespostaCandidaturaJpaRepository.class);
    private final CandidaturaJpaAdapter adapter = new CandidaturaJpaAdapter(candidaturas, respostas);

    @Test
    void traduzUnicidadeConcorrenteDaCandidatura() {
        when(candidaturas.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("uk"));
        assertThrows(CandidaturaRepositorio.CandidaturaDuplicadaException.class,
                () -> adapter.salvar(new Candidatura(java.util.UUID.randomUUID(), java.util.UUID.randomUUID())));
    }

    @Test
    void traduzUnicidadeDeRespostaNoLoteAtomico() {
        when(respostas.saveAllAndFlush(any())).thenThrow(new DataIntegrityViolationException("uk"));
        RespostaCandidatura resposta = new RespostaCandidatura(java.util.UUID.randomUUID(),
                java.util.UUID.randomUUID(), "valor");
        assertThrows(CandidaturaRepositorio.RespostasDuplicadasException.class,
                () -> adapter.salvarRespostasAtomicamente(Collections.singletonList(resposta)));
    }
}
