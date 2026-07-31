package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.app.ports.vaga.AutorizacaoVaga;
import br.com.pacto.recrutamento.core.enums.NomePapel;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

public class AutorizacaoVagaJpaAdapter implements AutorizacaoVaga {
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
}
