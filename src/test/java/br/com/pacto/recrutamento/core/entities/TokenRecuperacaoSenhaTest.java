package br.com.pacto.recrutamento.core.entities;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenRecuperacaoSenhaTest {
    @Test
    void tokenExpiradoOuUsadoNaoPodeSerConsumido() {
        TokenRecuperacaoSenha token = new TokenRecuperacaoSenha(UUID.randomUUID(), "hash", OffsetDateTime.now().minusMinutes(1));

        assertThat(token.podeSerConsumido(OffsetDateTime.now())).isFalse();
    }
}
