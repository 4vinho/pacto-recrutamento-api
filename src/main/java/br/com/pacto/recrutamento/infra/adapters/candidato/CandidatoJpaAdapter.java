package br.com.pacto.recrutamento.infra.adapters.candidato;

import br.com.pacto.recrutamento.infra.repositorys.candidato.CandidatoJpaRepository;

import br.com.pacto.recrutamento.infra.projections.candidato.CandidaturaPainelProjection;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.ports.candidato.CandidaturaDoCandidato;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.entities.Candidato;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CandidatoJpaAdapter implements CandidatoRepository {
    private final CandidatoJpaRepository repository;

    public CandidatoJpaAdapter(CandidatoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existePorUsuarioId(UUID usuarioId) {
        return repository.existsByUsuarioId(usuarioId);
    }

    @Override
    public Candidato salvar(Candidato candidato) {
        return repository.save(candidato);
    }

    @Override
    public Optional<Candidato> buscarPorUsuarioId(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    @Override
    public PaginaGenerico<CandidaturaDoCandidato> listarCandidaturasDoUsuario(
            UUID usuarioId, int page, int pageSize) {
        Page<CandidaturaPainelProjection> resultado = repository.listarPainel(
                usuarioId, PageRequest.of(page, pageSize));
        List<CandidaturaDoCandidato> itens = resultado.getContent().stream()
                .map(this::paraAplicacao)
                .collect(Collectors.toList());
        return new PaginaGenerico<>(itens, resultado.getTotalElements());
    }

    private CandidaturaDoCandidato paraAplicacao(CandidaturaPainelProjection projection) {
        return new CandidaturaDoCandidato(
                projection.getCandidaturaId(),
                projection.getVagaId(),
                projection.getTituloVaga(),
                StatusCandidatura.valueOf(projection.getStatus()),
                projection.getCriadaEm(),
                projection.getFeedback());
    }
}
