package br.com.pacto.recrutamento.infra.repositorys.candidatura;

import br.com.pacto.recrutamento.core.entities.HistoricoCandidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface HistoricoCandidaturaJpaRepository
        extends JpaRepository<HistoricoCandidatura, UUID> {
    List<HistoricoCandidatura> findAllByCandidaturaIdOrderByCriadoEmAsc(UUID candidaturaId);
}
