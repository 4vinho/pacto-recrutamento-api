package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Collections;
import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;

public class CandidaturaDTO {
    private final UUID id;
    private final UUID usuarioId;
    private final UUID vagaId;
    private final String tituloVaga;
    private final StatusCandidatura status;
    private final OffsetDateTime criadaEm;
    private final boolean perguntasRespondidas;
    private final boolean requisitosRespondidos;
    private final List<RespostaDetalhe> respostas;
    private final List<RequisitoDetalhe> requisitos;

    public CandidaturaDTO(UUID id, UUID usuarioId, UUID vagaId,
                          StatusCandidatura status, OffsetDateTime criadaEm) {
        this(id, usuarioId, vagaId, null, status, criadaEm, false, false,
                Collections.<RespostaDetalhe>emptyList(), Collections.<RequisitoDetalhe>emptyList());
    }

    public CandidaturaDTO(UUID id, UUID usuarioId, UUID vagaId, String tituloVaga,
                          StatusCandidatura status, OffsetDateTime criadaEm,
                          boolean perguntasRespondidas, boolean requisitosRespondidos) {
        this(id, usuarioId, vagaId, tituloVaga, status, criadaEm, perguntasRespondidas,
                requisitosRespondidos, Collections.<RespostaDetalhe>emptyList(),
                Collections.<RequisitoDetalhe>emptyList());
    }

    public CandidaturaDTO(UUID id, UUID usuarioId, UUID vagaId, String tituloVaga,
                          StatusCandidatura status, OffsetDateTime criadaEm,
                          boolean perguntasRespondidas, boolean requisitosRespondidos,
                          List<RespostaDetalhe> respostas, List<RequisitoDetalhe> requisitos) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
        this.tituloVaga = tituloVaga;
        this.status = status;
        this.criadaEm = criadaEm;
        this.perguntasRespondidas = perguntasRespondidas;
        this.requisitosRespondidos = requisitosRespondidos;
        this.respostas = respostas;
        this.requisitos = requisitos;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getVagaId() {
        return vagaId;
    }
    public String getTituloVaga() { return tituloVaga; }

    public StatusCandidatura getStatus() {
        return status;
    }

    public OffsetDateTime getCriadaEm() {
        return criadaEm;
    }

    public boolean isPerguntasRespondidas() { return perguntasRespondidas; }

    public boolean isRequisitosRespondidos() { return requisitosRespondidos; }
    public List<RespostaDetalhe> getRespostas() { return respostas; }
    public List<RequisitoDetalhe> getRequisitos() { return requisitos; }

    public static class RespostaDetalhe {
        private final String pergunta;
        private final String valor;
        public RespostaDetalhe(String pergunta, String valor) {
            this.pergunta = pergunta;
            this.valor = valor;
        }
        public String getPergunta() { return pergunta; }
        public String getValor() { return valor; }
    }

    public static class RequisitoDetalhe {
        private final String requisito;
        private final NivelAtendimentoRequisito nivel;
        public RequisitoDetalhe(String requisito, NivelAtendimentoRequisito nivel) {
            this.requisito = requisito;
            this.nivel = nivel;
        }
        public String getRequisito() { return requisito; }
        public NivelAtendimentoRequisito getNivel() { return nivel; }
    }
}
