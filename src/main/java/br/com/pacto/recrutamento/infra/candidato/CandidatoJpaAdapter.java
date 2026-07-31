package br.com.pacto.recrutamento.infra.candidato;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoPersistido;
import br.com.pacto.recrutamento.app.ports.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.ports.candidato.CandidaturaDoCandidato;
import br.com.pacto.recrutamento.app.ports.candidato.PaginaCandidaturas;
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
    private final CandidatoJpaMapper mapper;

    public CandidatoJpaAdapter(CandidatoJpaRepository repository, CandidatoJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public boolean existePorUsuarioId(UUID usuarioId) {
        return repository.existsByUsuarioId(usuarioId);
    }

    @Override
    public CandidatoPersistido salvar(UUID usuarioId, LocalDate dataAdmissao) {
        CandidatoJpaEntity entity = mapper.paraNovaEntidade(usuarioId, dataAdmissao);
        return mapper.paraAplicacao(repository.save(entity));
    }

    @Override
    public Optional<CandidatoPersistido> buscarPorUsuarioId(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId).map(mapper::paraAplicacao);
    }

    @Override
    public CandidatoPersistido atualizar(CandidatoPersistido candidato, LocalDate dataAdmissao) {
        CandidatoJpaEntity entity = repository.findById(candidato.getId())
                .orElseThrow(() -> new IllegalStateException("Candidato nao encontrado durante atualizacao"));
        entity.setDataAdmissao(dataAdmissao);
        return mapper.paraAplicacao(repository.save(entity));
    }

    @Override
    public PaginaCandidaturas listarCandidaturasDoUsuario(UUID usuarioId, int page, int pageSize) {
        Page<CandidaturaPainelProjection> resultado = repository.listarPainel(
                usuarioId, PageRequest.of(page, pageSize));
        List<CandidaturaDoCandidato> itens = resultado.getContent().stream()
                .map(mapper::paraAplicacao)
                .collect(Collectors.toList());
        return new PaginaCandidaturas(itens, resultado.getTotalElements());
    }
}
