package br.com.pacto.recrutamento.infra.usuario;

import br.com.pacto.recrutamento.app.ports.usuario.PapelPort;
import br.com.pacto.recrutamento.core.entities.Papel;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class PapelJpaAdapter implements PapelPort {
    private final PapelJpaRepository repository;
    public PapelJpaAdapter(PapelJpaRepository repository) { this.repository = repository; }
    public Optional<Papel> buscarPorNome(NomePapel nome) { return repository.findByNome(nome); }
}
