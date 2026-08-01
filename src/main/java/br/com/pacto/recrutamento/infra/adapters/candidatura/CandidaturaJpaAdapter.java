package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import br.com.pacto.recrutamento.core.entities.RespostaRequisitoCandidatura;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.CandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.RespostaCandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.RespostaRequisitoCandidaturaJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CandidaturaJpaAdapter implements CandidaturaPort {
    private final CandidaturaJpaRepository candidaturas;
    private final RespostaCandidaturaJpaRepository respostas;
    private final RespostaRequisitoCandidaturaJpaRepository respostasRequisitos;

    public CandidaturaJpaAdapter(CandidaturaJpaRepository candidaturas,
                                 RespostaCandidaturaJpaRepository respostas,
                                 RespostaRequisitoCandidaturaJpaRepository respostasRequisitos) {
        this.candidaturas = candidaturas;
        this.respostas = respostas;
        this.respostasRequisitos = respostasRequisitos;
    }

    public Optional<Candidatura> buscarPorId(UUID id) {
        return candidaturas.findById(id);
    }

    @Override
    public PaginaGenerico<Candidatura> listarPorVaga(UUID vagaId, StatusCandidatura status,
                                                      int page, int pageSize) {
        Specification<Candidatura> filtro = (root, query, builder) -> status == null
                ? builder.equal(root.get("vagaId"), vagaId)
                : builder.and(builder.equal(root.get("vagaId"), vagaId),
                        builder.equal(root.get("status"), status));
        Page<Candidatura> resultado = candidaturas.findAll(filtro, PageRequest.of(page, pageSize,
                Sort.by(Sort.Direction.DESC, "criadoEm")));
        return new PaginaGenerico<>(resultado.getContent(), resultado.getTotalElements());
    }

    @Override
    public PaginaGenerico<Candidatura> listarPorUsuario(UUID usuarioId, int page, int pageSize) {
        Page<Candidatura> resultado = candidaturas.findAll(
                (root, query, builder) -> builder.equal(root.get("usuarioId"), usuarioId),
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "criadoEm")));
        return new PaginaGenerico<>(resultado.getContent(), resultado.getTotalElements());
    }

    public Optional<Candidatura> buscarPorIdParaAtualizacao(UUID id) {
        return candidaturas.findByIdForUpdate(id);
    }

    public boolean existePorUsuarioIdEVagaId(UUID usuarioId, UUID vagaId) {
        return candidaturas.existsByUsuarioIdAndVagaId(usuarioId, vagaId);
    }

    public List<RespostaCandidatura> listarRespostas(UUID candidaturaId) {
        return respostas.findAllByCandidaturaId(candidaturaId);
    }

    public List<RespostaRequisitoCandidatura> listarRespostasRequisitos(UUID candidaturaId) {
        return respostasRequisitos.findAllByCandidaturaId(candidaturaId);
    }

    @Transactional
    public Candidatura salvar(Candidatura candidatura) {
        try {
            return candidaturas.saveAndFlush(candidatura);
        } catch (DataIntegrityViolationException ex) {
            throw new CandidaturaDuplicadaException();
        }
    }

    @Transactional
    public void registrarRespostasPerguntasAtomicamente(Candidatura candidatura,
                                                         List<RespostaCandidatura> lote) {
        try {
            respostas.saveAllAndFlush(lote);
            candidatura.registrarPerguntasRespondidas();
            candidaturas.saveAndFlush(candidatura);
        } catch (DataIntegrityViolationException ex) {
            throw new RespostasDuplicadasException();
        }
    }

    @Transactional
    public void registrarRespostasRequisitosAtomicamente(Candidatura candidatura,
            List<RespostaRequisitoCandidatura> lote) {
        try {
            respostasRequisitos.saveAllAndFlush(lote);
            candidatura.registrarRequisitosRespondidos();
            candidaturas.saveAndFlush(candidatura);
        } catch (DataIntegrityViolationException ex) {
            throw new RequisitosJaRespondidosException();
        }
    }
}
