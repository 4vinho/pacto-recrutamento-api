package br.com.pacto.recrutamento.app.ports.out.templatevaga;

import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;

import java.util.Optional;
import java.util.UUID;

public interface TemplateVagaPort {
    default PaginaGenerico<TemplateVaga> listar(String busca, int page, int pageSize) {
        throw new UnsupportedOperationException();
    }

    Optional<TemplateVaga> buscarAtivoPorId(UUID id);

    TemplateVaga salvar(TemplateVaga template);
}
