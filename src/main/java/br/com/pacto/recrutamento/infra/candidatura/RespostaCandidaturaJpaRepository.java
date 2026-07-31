package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface RespostaCandidaturaJpaRepository extends JpaRepository<RespostaCandidatura, UUID> {}
