package br.com.pacto.recrutamento.infra.adapters.usuario;

import br.com.pacto.recrutamento.infra.repositorys.usuario.RefreshTokenJpaRepository;

import br.com.pacto.recrutamento.app.ports.out.usuario.RefreshTokenPort;
import br.com.pacto.recrutamento.core.entities.RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenJpaAdapter implements RefreshTokenPort {
    private final RefreshTokenJpaRepository repository;

    public RefreshTokenJpaAdapter(RefreshTokenJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<RefreshToken> buscarPorHash(String hash) {
        return repository.findByTokenHash(hash);
    }

    public void salvar(RefreshToken token) {
        repository.save(token);
    }

    @Transactional
    public void revogarFamilia(UUID familiaId, OffsetDateTime data) {
        repository.revogarFamilia(familiaId, data);
    }

    @Transactional
    public void revogarTodosDoUsuario(UUID usuarioId, OffsetDateTime data) {
        repository.revogarTodosDoUsuario(usuarioId, data);
    }
}
