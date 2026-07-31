package br.com.pacto.recrutamento.infra.usuario;

import br.com.pacto.recrutamento.core.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

interface UsuarioJpaRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
}
