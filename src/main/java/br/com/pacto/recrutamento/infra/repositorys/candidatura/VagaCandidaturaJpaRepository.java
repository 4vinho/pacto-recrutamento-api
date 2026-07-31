package br.com.pacto.recrutamento.infra.repositorys.candidatura;

import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VagaCandidaturaJpaRepository extends JpaRepository<Vaga, UUID> {
}
