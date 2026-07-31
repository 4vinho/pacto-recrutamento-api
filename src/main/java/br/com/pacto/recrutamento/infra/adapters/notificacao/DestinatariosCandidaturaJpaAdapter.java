package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.ports.out.notificacao.DestinatariosCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.notificacao.model.DestinatariosCandidatura;
import br.com.pacto.recrutamento.infra.repositorys.notificacao.DestinatariosCandidaturaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class DestinatariosCandidaturaJpaAdapter implements DestinatariosCandidaturaPort {
    private final DestinatariosCandidaturaRepository repository;

    public DestinatariosCandidaturaJpaAdapter(DestinatariosCandidaturaRepository repository) {
        this.repository = repository;
    }

    public Optional<DestinatariosCandidatura> buscarPorCandidatura(UUID candidaturaId) {
        return repository.buscarPorCandidatura(candidaturaId)
                .map(projecao -> new DestinatariosCandidatura(
                        projecao.getResponsavelId(),
                        projecao.getCandidatoId()
                ));
    }
}
