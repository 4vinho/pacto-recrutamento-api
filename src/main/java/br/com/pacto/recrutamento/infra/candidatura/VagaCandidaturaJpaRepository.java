package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.infra.vaga.VagaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface VagaCandidaturaJpaRepository extends JpaRepository<VagaJpaEntity, UUID> {}
