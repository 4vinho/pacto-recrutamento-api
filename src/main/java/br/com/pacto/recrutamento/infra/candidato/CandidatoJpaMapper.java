package br.com.pacto.recrutamento.infra.candidato;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoPersistido;
import br.com.pacto.recrutamento.app.ports.candidato.CandidaturaDoCandidato;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
class CandidatoJpaMapper {

    CandidatoJpaEntity paraNovaEntidade(UUID usuarioId, LocalDate dataAdmissao) {
        return new CandidatoJpaEntity(UUID.randomUUID(), usuarioId, dataAdmissao);
    }

    CandidatoPersistido paraAplicacao(CandidatoJpaEntity entity) {
        return new CandidatoPersistido(entity.getId(), entity.getUsuarioId(), entity.getDataAdmissao());
    }

    CandidaturaDoCandidato paraAplicacao(CandidaturaPainelProjection projection) {
        return new CandidaturaDoCandidato(
                projection.getCandidaturaId(),
                projection.getVagaId(),
                projection.getTituloVaga(),
                StatusCandidatura.valueOf(projection.getStatus()),
                projection.getCriadaEm(),
                projection.getFeedback());
    }
}
