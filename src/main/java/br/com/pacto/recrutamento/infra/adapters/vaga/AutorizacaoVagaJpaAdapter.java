package br.com.pacto.recrutamento.infra.adapters.vaga;

import br.com.pacto.recrutamento.app.ports.out.vaga.AutorizacaoVagaPort;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import br.com.pacto.recrutamento.infra.repositorys.vaga.AutorizacaoVagaJpaRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

public class AutorizacaoVagaJpaAdapter implements AutorizacaoVagaPort {
    private final AutorizacaoVagaJpaRepository repository;
    private final Collection<NomePapel> papeisPermitidos;

    public AutorizacaoVagaJpaAdapter(AutorizacaoVagaJpaRepository repository,
                                     Collection<NomePapel> papeisPermitidos) {
        this.repository = repository;
        this.papeisPermitidos = Collections.unmodifiableSet(EnumSet.copyOf(papeisPermitidos));
    }

    @Override
    public boolean podeManterVagas(UUID usuarioId) {
        return usuarioId != null && repository.possuiPapelAtivo(usuarioId, papeisPermitidos);
    }

    @Override
    public boolean podeExcluirVagas(UUID usuarioId) {
        return usuarioId != null && repository.possuiPapelAtivo(usuarioId,
                Collections.singleton(NomePapel.ADMINISTRADOR));
    }
}
