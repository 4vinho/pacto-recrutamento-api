package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.ports.out.notificacao.DestinatariosCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.notificacao.model.DestinatariosCandidatura;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.CandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.vaga.VagaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.usuario.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class DestinatariosCandidaturaJpaAdapter implements DestinatariosCandidaturaPort {
    private final CandidaturaJpaRepository candidaturas;
    private final VagaJpaRepository vagas;
    private final UsuarioJpaRepository usuarios;

    public DestinatariosCandidaturaJpaAdapter(CandidaturaJpaRepository candidaturas,
                                              VagaJpaRepository vagas,
                                              UsuarioJpaRepository usuarios) {
        this.candidaturas = candidaturas;
        this.vagas = vagas;
        this.usuarios = usuarios;
    }

    public Optional<DestinatariosCandidatura> buscarPorCandidatura(UUID candidaturaId) {
        Optional<Candidatura> candidatura = candidaturas.findById(candidaturaId);
        if (!candidatura.isPresent()) return Optional.empty();
        Optional<Vaga> vaga = vagas.findById(candidatura.get().getVagaId());
        if (!vaga.isPresent()) return Optional.empty();
        String nomeCandidato = usuarios.findById(candidatura.get().getUsuarioId())
                .map(usuario -> usuario.getNome() == null ? usuario.getEmail() : usuario.getNome())
                .orElse("Candidato");
        return Optional.of(new DestinatariosCandidatura(
                vaga.get().getResponsaveisIds(), candidatura.get().getUsuarioId(),
                vaga.get().getTitulo(), nomeCandidato));
    }
}
