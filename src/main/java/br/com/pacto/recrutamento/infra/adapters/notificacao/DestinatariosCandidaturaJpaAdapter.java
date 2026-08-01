package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.ports.out.notificacao.DestinatariosCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.notificacao.model.DestinatariosCandidatura;
import br.com.pacto.recrutamento.infra.repositorys.notificacao.DestinatariosCandidaturaRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DestinatariosCandidaturaJpaAdapter implements DestinatariosCandidaturaPort {
    private final DestinatariosCandidaturaRepository repository;

    public DestinatariosCandidaturaJpaAdapter(DestinatariosCandidaturaRepository repository) {
        this.repository = repository;
    }

    public Optional<DestinatariosCandidatura> buscarPorCandidatura(UUID candidaturaId) {
        List<br.com.pacto.recrutamento.infra.projections.DestinatariosCandidaturaProjection> projecoes =
                repository.buscarPorCandidatura(candidaturaId);
        if (projecoes.isEmpty()) return Optional.empty();
        LinkedHashSet<UUID> responsaveis = new LinkedHashSet<>();
        for (br.com.pacto.recrutamento.infra.projections.DestinatariosCandidaturaProjection projecao : projecoes) {
            responsaveis.add(projecao.getResponsavelId());
        }
        return Optional.of(new DestinatariosCandidatura(
                responsaveis, projecoes.get(0).getUsuarioId()));
    }
}
