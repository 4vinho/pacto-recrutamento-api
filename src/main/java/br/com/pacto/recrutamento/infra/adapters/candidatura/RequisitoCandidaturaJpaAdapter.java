package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.app.ports.out.candidatura.RequisitoCandidaturaPort;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.infra.repositorys.vaga.RequisitoVagaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class RequisitoCandidaturaJpaAdapter implements RequisitoCandidaturaPort {
    private final RequisitoVagaJpaRepository repository;

    public RequisitoCandidaturaJpaAdapter(RequisitoVagaJpaRepository repository) {
        this.repository = repository;
    }

    public List<RequisitoVaga> listarAtivosPorVagaId(UUID vagaId) {
        return repository.findAllByVagaIdAndExcluidoEmIsNull(vagaId);
    }
}
