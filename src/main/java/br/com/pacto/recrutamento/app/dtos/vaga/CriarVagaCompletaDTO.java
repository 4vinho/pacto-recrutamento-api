package br.com.pacto.recrutamento.app.dtos.vaga;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CriarVagaCompletaDTO {
    private final UUID usuarioSolicitanteId;
    private final Collection<UUID> responsaveisIds;
    private final String titulo;
    private final String descricao;
    private final List<Pergunta> perguntas;
    private final List<Requisito> requisitos;

    public CriarVagaCompletaDTO(UUID usuarioSolicitanteId, Collection<UUID> responsaveisIds,
                                String titulo, String descricao, List<Pergunta> perguntas,
                                List<Requisito> requisitos) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.responsaveisIds = responsaveisIds;
        this.titulo = titulo;
        this.descricao = descricao;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
    }

    public UUID getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public Collection<UUID> getResponsaveisIds() { return responsaveisIds; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public List<Pergunta> getPerguntas() { return perguntas; }
    public List<Requisito> getRequisitos() { return requisitos; }

    public static class Pergunta {
        private final String enunciado;
        private final TipoResposta tipoResposta;
        private final boolean obrigatoria;
        private final int ordem;

        public Pergunta(String enunciado, TipoResposta tipoResposta, boolean obrigatoria, int ordem) {
            this.enunciado = enunciado;
            this.tipoResposta = tipoResposta;
            this.obrigatoria = obrigatoria;
            this.ordem = ordem;
        }

        public String getEnunciado() { return enunciado; }
        public TipoResposta getTipoResposta() { return tipoResposta; }
        public boolean isObrigatoria() { return obrigatoria; }
        public int getOrdem() { return ordem; }
    }

    public static class Requisito {
        private final String descricao;
        private final boolean obrigatorio;

        public Requisito(String descricao, boolean obrigatorio) {
            this.descricao = descricao;
            this.obrigatorio = obrigatorio;
        }

        public String getDescricao() { return descricao; }
        public boolean isObrigatorio() { return obrigatorio; }
    }
}
