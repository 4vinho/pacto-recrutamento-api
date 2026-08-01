package br.com.pacto.recrutamento.infra.adapters.templatevaga;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.TemplateVagaPort;
import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.infra.repositorys.templatevaga.TemplateVagaJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;

import java.util.Optional;
import java.util.UUID;

@Repository
class TemplateVagaJpaAdapter implements TemplateVagaPort {
    private final TemplateVagaJpaRepository repository;

    TemplateVagaJpaAdapter(TemplateVagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<TemplateVaga> buscarAtivoPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    public PaginaGenerico<TemplateVaga> listar(String busca, int page, int pageSize) {
        Specification<TemplateVaga> filtro = (root, query, builder) -> {
            Predicate predicate = builder.isNull(root.get("excluidoEm"));
            if (busca != null && !busca.trim().isEmpty()) {
                String termo = "%" + busca.trim().toLowerCase() + "%";
                predicate = builder.and(predicate, builder.or(
                        builder.like(builder.lower(root.get("titulo")), termo),
                        builder.like(builder.lower(root.get("descricao")), termo)));
            }
            return predicate;
        };
        Page<TemplateVaga> resultado = repository.findAll(filtro, PageRequest.of(page, pageSize,
                Sort.by(Sort.Direction.DESC, "criadoEm")));
        return new PaginaGenerico<>(resultado.getContent(), resultado.getTotalElements());
    }

    public TemplateVaga salvar(TemplateVaga template) {
        return repository.save(template);
    }
}
