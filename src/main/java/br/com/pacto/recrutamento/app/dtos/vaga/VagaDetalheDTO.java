package br.com.pacto.recrutamento.app.dtos.vaga;

import br.com.pacto.recrutamento.core.enums.StatusVaga;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class VagaDetalheDTO extends VagaDTO {
    private final List<RequisitoVagaDTO> requisitos;
    private final List<PerguntaVagaDTO> perguntas;

    public VagaDetalheDTO(UUID id, Collection<UUID> responsaveisIds, String titulo,
                          String descricao, StatusVaga status,
                          List<RequisitoVagaDTO> requisitos, List<PerguntaVagaDTO> perguntas) {
        super(id, responsaveisIds, titulo, descricao, status);
        this.requisitos = requisitos;
        this.perguntas = perguntas;
    }

    public List<RequisitoVagaDTO> getRequisitos() { return requisitos; }
    public List<PerguntaVagaDTO> getPerguntas() { return perguntas; }
}
