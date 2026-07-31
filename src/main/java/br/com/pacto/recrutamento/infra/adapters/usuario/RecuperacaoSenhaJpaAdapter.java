package br.com.pacto.recrutamento.infra.adapters.usuario;

import br.com.pacto.recrutamento.infra.repositorys.usuario.TokenRecuperacaoSenhaJpaRepository;

import br.com.pacto.recrutamento.app.ports.usuario.RecuperacaoSenhaPort;
import br.com.pacto.recrutamento.core.entities.TokenRecuperacaoSenha;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class RecuperacaoSenhaJpaAdapter implements RecuperacaoSenhaPort {
    private final TokenRecuperacaoSenhaJpaRepository repository;

    public RecuperacaoSenhaJpaAdapter(TokenRecuperacaoSenhaJpaRepository repository) {
        this.repository = repository;
    }

    public void salvar(UUID usuarioId, String tokenHash, OffsetDateTime expiraEm) {
        repository.save(new TokenRecuperacaoSenha(usuarioId, tokenHash, expiraEm));
    }

    @Transactional
    public Optional<UUID> consumirTokenValido(String tokenHash, OffsetDateTime agora) {
        Optional<TokenRecuperacaoSenha> token = repository.findByTokenHash(tokenHash);
        if (!token.isPresent() || !token.get().podeSerConsumido(agora)) return Optional.empty();
        if (repository.consumirSeValido(token.get().getId(), agora) != 1) return Optional.empty();
        return Optional.of(token.get().getUsuarioId());
    }
}
