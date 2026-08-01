package br.com.pacto.recrutamento.infra.adapters.vaga;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.VagaTemplatePort;
import br.com.pacto.recrutamento.app.ports.out.vaga.VagaPort;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.enums.StatusVaga;
import br.com.pacto.recrutamento.infra.repositorys.vaga.VagaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;

@Repository
public class VagaJpaAdapter implements VagaPort, VagaTemplatePort {
    private final VagaJpaRepository repository;

    public VagaJpaAdapter(VagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<Vaga> buscarAtivaPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    @Override
    public PaginaGenerico<Vaga> listar(String busca, StatusVaga status, int page, int pageSize,
                                       String ordenarPor, boolean ascendente,
                                       UUID excluirCandidaturasDoUsuarioId) {
        Sort sort = Sort.by(ascendente ? Sort.Direction.ASC : Sort.Direction.DESC, ordenarPor);
        Specification<Vaga> filtro = (root, query, builder) -> {
            Predicate predicate = builder.isNull(root.get("excluidoEm"));
            if (status != null) predicate = builder.and(predicate, builder.equal(root.get("status"), status));
            if (busca != null && !busca.trim().isEmpty()) {
                String termo = "%" + busca.trim().toLowerCase() + "%";
                predicate = builder.and(predicate, builder.or(
                        builder.like(builder.lower(root.get("titulo")), termo),
                        builder.like(builder.lower(root.get("descricao")), termo)));
            }
            if (excluirCandidaturasDoUsuarioId != null) {
                Subquery<UUID> candidaturas = query.subquery(UUID.class);
                javax.persistence.criteria.Root<Candidatura> candidatura = candidaturas.from(Candidatura.class);
                candidaturas.select(candidatura.get("vagaId")).where(
                        builder.equal(candidatura.get("usuarioId"), excluirCandidaturasDoUsuarioId));
                predicate = builder.and(predicate, builder.not(root.get("id").in(candidaturas)));
            }
            return predicate;
        };
        Page<Vaga> resultado = repository.findAll(filtro, PageRequest.of(page, pageSize, sort));
        return new PaginaGenerico<>(resultado.getContent(), resultado.getTotalElements());
    }

    public Vaga salvar(Vaga vaga) {
        return repository.save(vaga);
    }
}
