package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import br.com.pacto.recrutamento.core.entities.RespostaRequisitoCandidatura;
import br.com.pacto.recrutamento.core.entities.HistoricoCandidatura;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.CandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.RespostaCandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.RespostaRequisitoCandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.HistoricoCandidaturaJpaRepository;
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
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.Map;
import javax.persistence.criteria.Subquery;

@Repository
public class CandidaturaJpaAdapter implements CandidaturaPort {
    private final CandidaturaJpaRepository candidaturas;
    private final RespostaCandidaturaJpaRepository respostas;
    private final RespostaRequisitoCandidaturaJpaRepository respostasRequisitos;
    private final HistoricoCandidaturaJpaRepository historicos;

    public CandidaturaJpaAdapter(CandidaturaJpaRepository candidaturas,
                                 RespostaCandidaturaJpaRepository respostas,
                                 RespostaRequisitoCandidaturaJpaRepository respostasRequisitos,
                                 HistoricoCandidaturaJpaRepository historicos) {
        this.candidaturas = candidaturas;
        this.respostas = respostas;
        this.respostasRequisitos = respostasRequisitos;
        this.historicos = historicos;
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
        return listarPorUsuario(usuarioId, null, null, null, page, pageSize);
    }

    @Override
    public PaginaGenerico<Candidatura> listarPorUsuario(UUID usuarioId,
            StatusCandidatura status, OffsetDateTime inicio, OffsetDateTime fim,
            int page, int pageSize) {
        Page<Candidatura> resultado = candidaturas.findAll(
                (root, query, builder) -> {
                    javax.persistence.criteria.Predicate filtro = builder.equal(root.get("usuarioId"), usuarioId);
                    if (status != null) filtro = builder.and(filtro, builder.equal(root.get("status"), status));
                    if (inicio != null) filtro = builder.and(filtro, builder.greaterThanOrEqualTo(root.get("criadoEm"), inicio));
                    if (fim != null) filtro = builder.and(filtro, builder.lessThanOrEqualTo(root.get("criadoEm"), fim));
                    return filtro;
                },
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "criadoEm")));
        return new PaginaGenerico<>(resultado.getContent(), resultado.getTotalElements());
    }

    @Override
    public PaginaGenerico<Candidatura> listarPorVaga(UUID vagaId, StatusCandidatura status,
            String busca, UUID requisitoId, NivelAtendimentoRequisito nivelMinimo,
            Integer tempoEmpresaMeses, Boolean atendeTodosRequisitos,
            int page, int pageSize) {
        Specification<Candidatura> filtro = (root, query, builder) -> {
            javax.persistence.criteria.Predicate predicate = builder.equal(root.get("vagaId"), vagaId);
            if (status != null) predicate = builder.and(predicate, builder.equal(root.get("status"), status));
            if (busca != null && !busca.trim().isEmpty()) {
                String termo = "%" + busca.trim().toLowerCase() + "%";
                Subquery<UUID> sub = query.subquery(UUID.class);
                javax.persistence.criteria.Root<br.com.pacto.recrutamento.core.entities.Usuario> usuario =
                        sub.from(br.com.pacto.recrutamento.core.entities.Usuario.class);
                sub.select(usuario.get("id")).where(builder.or(
                        builder.like(builder.lower(usuario.get("nome")), termo),
                        builder.like(builder.lower(usuario.get("email")), termo)));
                predicate = builder.and(predicate, root.get("usuarioId").in(sub));
            }
            if (requisitoId != null && nivelMinimo != null) {
                Subquery<UUID> sub = query.subquery(UUID.class);
                javax.persistence.criteria.Root<RespostaRequisitoCandidatura> resposta = sub.from(RespostaRequisitoCandidatura.class);
                java.util.List<NivelAtendimentoRequisito> niveis = niveisAPartirDe(nivelMinimo);
                sub.select(resposta.get("candidaturaId")).where(builder.and(
                        builder.equal(resposta.get("requisitoId"), requisitoId),
                        resposta.get("nivel").in(niveis)));
                predicate = builder.and(predicate, root.get("id").in(sub));
            }
            if (Boolean.TRUE.equals(atendeTodosRequisitos)) {
                Subquery<UUID> sub = query.subquery(UUID.class);
                javax.persistence.criteria.Root<RespostaRequisitoCandidatura> resposta = sub.from(RespostaRequisitoCandidatura.class);
                sub.select(resposta.get("candidaturaId")).where(builder.not(
                        resposta.get("nivel").in(niveisAPartirDe(NivelAtendimentoRequisito.ALTO))));
                predicate = builder.and(predicate, builder.not(root.get("id").in(sub)));
            }
            if (tempoEmpresaMeses != null) {
                java.time.LocalDate limite = java.time.LocalDate.now().minusMonths(tempoEmpresaMeses);
                Subquery<UUID> sub = query.subquery(UUID.class);
                javax.persistence.criteria.Root<br.com.pacto.recrutamento.core.entities.Usuario> usuario =
                        sub.from(br.com.pacto.recrutamento.core.entities.Usuario.class);
                sub.select(usuario.get("id")).where(builder.lessThanOrEqualTo(usuario.get("dataAdmissao"), limite));
                predicate = builder.and(predicate, root.get("usuarioId").in(sub));
            }
            return predicate;
        };
        Page<Candidatura> resultado = candidaturas.findAll(filtro, PageRequest.of(page, pageSize,
                Sort.by(Sort.Direction.DESC, "criadoEm")));
        return new PaginaGenerico<>(resultado.getContent(), resultado.getTotalElements());
    }

    @Override
    public Map<StatusCandidatura, Long> contarPorStatusDoUsuario(UUID usuarioId,
            OffsetDateTime inicio, OffsetDateTime fim) {
        EnumMap<StatusCandidatura, Long> totais = new EnumMap<>(StatusCandidatura.class);
        for (Candidatura candidatura : candidaturas.findAll((root, query, builder) -> {
            javax.persistence.criteria.Predicate p = builder.equal(root.get("usuarioId"), usuarioId);
            if (inicio != null) p = builder.and(p, builder.greaterThanOrEqualTo(root.get("criadoEm"), inicio));
            if (fim != null) p = builder.and(p, builder.lessThanOrEqualTo(root.get("criadoEm"), fim));
            return p;
        })) totais.merge(candidatura.getStatus(), 1L, Long::sum);
        return totais;
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

    public List<HistoricoCandidatura> listarHistorico(UUID candidaturaId) {
        return historicos.findAllByCandidaturaIdOrderByCriadoEmAsc(candidaturaId);
    }

    public void salvarHistorico(HistoricoCandidatura historico) {
        historicos.saveAndFlush(historico);
    }

    private java.util.List<NivelAtendimentoRequisito> niveisAPartirDe(
            NivelAtendimentoRequisito minimo) {
        NivelAtendimentoRequisito[] valores = NivelAtendimentoRequisito.values();
        return java.util.Arrays.asList(valores).subList(minimo.ordinal(), valores.length);
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
