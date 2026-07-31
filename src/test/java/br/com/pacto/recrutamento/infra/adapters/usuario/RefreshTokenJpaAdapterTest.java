package br.com.pacto.recrutamento.infra.adapters.usuario;

import br.com.pacto.recrutamento.core.entities.RefreshToken;
import br.com.pacto.recrutamento.infra.repositorys.usuario.RefreshTokenJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenJpaAdapterTest {
    private final RefreshTokenJpaRepository repository = mock(RefreshTokenJpaRepository.class);
    private final RefreshTokenJpaAdapter adapter = new RefreshTokenJpaAdapter(repository);

    @Test
    void buscaTokenComBloqueioParaRotacao() {
        String hash = "a";
        RefreshToken token = new RefreshToken(UUID.randomUUID(), hash, UUID.randomUUID(),
                OffsetDateTime.now().plusDays(1));
        when(repository.findByTokenHashForUpdate(hash)).thenReturn(Optional.of(token));

        Optional<RefreshToken> encontrado = adapter.buscarPorHashParaAtualizacao(hash);

        assertSame(token, encontrado.get());
        verify(repository).findByTokenHashForUpdate(hash);
    }

    @Test
    void repositorioExigeBloqueioPessimistaNaBuscaDeRotacao() throws Exception {
        Lock lock = RefreshTokenJpaRepository.class
                .getMethod("findByTokenHashForUpdate", String.class)
                .getAnnotation(Lock.class);

        assertEquals(LockModeType.PESSIMISTIC_WRITE, lock.value());
    }
}
