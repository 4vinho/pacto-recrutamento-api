package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
class CandidaturaJpaMapper {
    CandidaturaJpaEntity paraEntidade(Candidatura origem) {
        return new CandidaturaJpaEntity(origem.getId(), origem.getCandidatoId(), origem.getVagaId(),
                origem.getStatus(), origem.getCriadoEm(), origem.getAtualizadoEm(), origem.getCanceladoEm());
    }
    Candidatura paraDominio(CandidaturaProjection origem) {
        Candidatura destino = new Candidatura();
        destino.setId(origem.getId()); destino.setCandidatoId(origem.getCandidatoId());
        destino.setVagaId(origem.getVagaId()); destino.setCriadoEm(origem.getCriadoEm());
        destino.setAtualizadoEm(origem.getAtualizadoEm()); destino.setCanceladoEm(origem.getCanceladoEm());
        restaurarStatus(destino, origem.getStatus());
        return destino;
    }
    Candidatura paraDominio(CandidaturaJpaEntity origem) {
        return paraDominio(new EntidadeProjection(origem));
    }
    RespostaCandidaturaJpaEntity paraEntidade(RespostaCandidatura origem) {
        return new RespostaCandidaturaJpaEntity(origem.getId(), origem.getCandidaturaId(),
                origem.getPerguntaId(), origem.getValor(), origem.getCriadoEm(), origem.getAtualizadoEm());
    }
    private void restaurarStatus(Candidatura destino, br.com.pacto.recrutamento.core.enums.StatusCandidatura status) {
        if (status == br.com.pacto.recrutamento.core.enums.StatusCandidatura.ENVIADA) return;
        destino.setStatus(br.com.pacto.recrutamento.core.enums.StatusCandidatura.EM_ANALISE);
        if (status != br.com.pacto.recrutamento.core.enums.StatusCandidatura.EM_ANALISE) destino.setStatus(status);
    }
    private static final class EntidadeProjection implements CandidaturaProjection {
        private final CandidaturaJpaEntity e;
        private EntidadeProjection(CandidaturaJpaEntity e) { this.e = e; }
        public UUID getId(){return e.getId();} public UUID getCandidatoId(){return e.getCandidatoId();}
        public UUID getVagaId(){return e.getVagaId();} public br.com.pacto.recrutamento.core.enums.StatusCandidatura getStatus(){return e.getStatus();}
        public java.time.OffsetDateTime getCriadoEm(){return e.getCriadoEm();} public java.time.OffsetDateTime getAtualizadoEm(){return e.getAtualizadoEm();}
        public java.time.OffsetDateTime getCanceladoEm(){return e.getCanceladoEm();}
    }
}
