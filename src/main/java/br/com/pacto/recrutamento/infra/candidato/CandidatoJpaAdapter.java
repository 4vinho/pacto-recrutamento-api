package br.com.pacto.recrutamento.infra.candidato;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoPersistido;
import br.com.pacto.recrutamento.app.ports.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.ports.candidato.CandidaturaDoCandidato;
import br.com.pacto.recrutamento.app.ports.candidato.PaginaCandidaturas;
import br.com.pacto.recrutamento.core.entities.Candidato;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    public CandidatoPersistido salvar(UUID usuarioId, LocalDate dataAdmissao) {
        return paraAplicacao(repository.save(new Candidato(usuarioId, dataAdmissao)));
    }

    @Override
    public Optional<CandidatoPersistido> buscarPorUsuarioId(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId).map(this::paraAplicacao);
    }

    @Override
    public CandidatoPersistido atualizar(CandidatoPersistido candidato, LocalDate dataAdmissao) {
        Candidato entity = repository.findById(candidato.getId())
                .orElseThrow(() -> new IllegalStateException("Candidato nao encontrado durante atualizacao"));
        entity.setDataAdmissao(dataAdmissao);
        return paraAplicacao(repository.save(entity));
    }

    @Override
    public PaginaCandidaturas listarCandidaturasDoUsuario(UUID usuarioId, int page, int pageSize) {
        Page<CandidaturaPainelProjection> resultado = repository.listarPainel(
                usuarioId, PageRequest.of(page, pageSize));
        List<CandidaturaDoCandidato> itens = resultado.getContent().stream()
                .map(this::paraAplicacao)
                .collect(Collectors.toList());
        return new PaginaCandidaturas(itens, resultado.getTotalElements());
    }

    private CandidatoPersistido paraAplicacao(Candidato candidato) {
        return new CandidatoPersistido(
                candidato.getId(), candidato.getUsuarioId(), candidato.getDataAdmissao());
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
