package br.com.pacto.recrutamento.infra.repositorys.usuario;

import br.com.pacto.recrutamento.core.entities.Usuario;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);

    @Query("select u.id from Usuario u join u.papeis p "
            + "where u.ativo = true and u.excluidoEm is null and p.nome = :papel")
    List<UUID> findIdsByPapelAtivo(@Param("papel") NomePapel papel);
}
