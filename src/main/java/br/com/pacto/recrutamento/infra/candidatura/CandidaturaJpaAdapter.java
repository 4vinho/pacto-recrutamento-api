package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.app.ports.candidatura.CandidaturaRepositorio;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CandidaturaJpaAdapter implements CandidaturaRepositorio {
    private final CandidaturaJpaRepository candidaturas;
    private final RespostaCandidaturaJpaRepository respostas;
    private final CandidaturaJpaMapper mapper;

    public CandidaturaJpaAdapter(CandidaturaJpaRepository candidaturas,
                                 RespostaCandidaturaJpaRepository respostas,
                                 CandidaturaJpaMapper mapper) {
        this.candidaturas = candidaturas; this.respostas = respostas; this.mapper = mapper;
    }
    public Optional<Candidatura> buscarPorId(UUID id) {
        return candidaturas.findProjectedById(id).map(mapper::paraDominio);
    }
    public boolean existePorCandidatoIdEVagaId(UUID candidatoId, UUID vagaId) {
        return candidaturas.existsByCandidatoIdAndVagaId(candidatoId, vagaId);
    }
    @Transactional
    public Candidatura salvar(Candidatura candidatura) {
        try {
            return mapper.paraDominio(candidaturas.saveAndFlush(mapper.paraEntidade(candidatura)));
        } catch (DataIntegrityViolationException ex) {
            throw new CandidaturaDuplicadaException();
        }
    }
    @Transactional
    public void salvarRespostasAtomicamente(List<RespostaCandidatura> lote) {
        try {
            respostas.saveAllAndFlush(lote.stream().map(mapper::paraEntidade).collect(Collectors.toList()));
        } catch (DataIntegrityViolationException ex) {
            throw new RespostasDuplicadasException();
        }
    }
}
