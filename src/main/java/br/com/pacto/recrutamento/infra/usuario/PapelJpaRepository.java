package br.com.pacto.recrutamento.infra.usuario;

import br.com.pacto.recrutamento.core.entities.Papel;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface PapelJpaRepository extends JpaRepository<Papel, UUID> {
    Optional<Papel> findByNome(NomePapel nome);
}
