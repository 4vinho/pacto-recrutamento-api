package br.com.pacto.recrutamento.infra.adapters.usuario;

import br.com.pacto.recrutamento.core.entities.TokenRecuperacaoSenha;
import br.com.pacto.recrutamento.infra.repositorys.usuario.TokenRecuperacaoSenhaJpaRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class RecuperacaoSenhaJpaAdapterTest {
    @Test
    void consumoAtomicoImpedeReutilizacaoDoMesmoToken() {
        UUID usuarioId = UUID.randomUUID();
        TokenRecuperacaoSenha token = new TokenRecuperacaoSenha(usuarioId, "hash", OffsetDateTime.now().plusMinutes(1));
        AtomicBoolean consumido = new AtomicBoolean();
        RecuperacaoSenhaJpaAdapter adapter = new RecuperacaoSenhaJpaAdapter(repositorio(token, consumido));

        assertThat(adapter.consumirTokenValido("hash", OffsetDateTime.now())).contains(usuarioId);
        assertThat(adapter.consumirTokenValido("hash", OffsetDateTime.now())).isEmpty();
    }

    @Test
    void tokenExpiradoNaoEConsumido() {
        TokenRecuperacaoSenha token = new TokenRecuperacaoSenha(UUID.randomUUID(), "hash", OffsetDateTime.now().minusSeconds(1));
        AtomicBoolean consumido = new AtomicBoolean();

        assertThat(new RecuperacaoSenhaJpaAdapter(repositorio(token, consumido))
                .consumirTokenValido("hash", OffsetDateTime.now())).isEmpty();
        assertThat(consumido).isFalse();
    }

    private TokenRecuperacaoSenhaJpaRepository repositorio(TokenRecuperacaoSenha token, AtomicBoolean consumido) {
        return (TokenRecuperacaoSenhaJpaRepository) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{TokenRecuperacaoSenhaJpaRepository.class},
                (proxy, method, args) -> {
                    if ("findByTokenHash".equals(method.getName())) return Optional.of(token);
                    if ("consumirSeValido".equals(method.getName()))
                        return consumido.compareAndSet(false, true) ? 1 : 0;
                    if ("save".equals(method.getName())) return args[0];
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
