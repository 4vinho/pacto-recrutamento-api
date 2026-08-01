package br.com.pacto.recrutamento.infra.repositorys.candidatura;

import br.com.pacto.recrutamento.core.entities.RespostaRequisitoCandidatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface RespostaRequisitoCandidaturaJpaRepository
        extends JpaRepository<RespostaRequisitoCandidatura, UUID> {
    List<RespostaRequisitoCandidatura> findAllByCandidaturaId(UUID candidaturaId);
}
