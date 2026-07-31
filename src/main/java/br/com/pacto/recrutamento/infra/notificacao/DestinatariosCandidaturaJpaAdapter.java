package br.com.pacto.recrutamento.infra.notificacao;
import br.com.pacto.recrutamento.app.notificacao.DestinatariosCandidatura;
import br.com.pacto.recrutamento.app.notificacao.DestinatariosCandidaturaPort;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;
@Component
public class DestinatariosCandidaturaJpaAdapter implements DestinatariosCandidaturaPort {
    private final DestinatariosCandidaturaRepository repository;
    public DestinatariosCandidaturaJpaAdapter(DestinatariosCandidaturaRepository repository) { this.repository = repository; }
    public Optional<DestinatariosCandidatura> buscarPorCandidatura(UUID candidaturaId) {
        return repository.buscarPorCandidatura(candidaturaId).map(p -> new DestinatariosCandidatura(p.getResponsavelId(), p.getCandidatoId()));
    }
}
