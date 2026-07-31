package br.com.pacto.recrutamento.app.candidato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaginaCandidaturas {
    private final List<CandidaturaDoCandidato> itens;
    private final long totalItens;

    public PaginaCandidaturas(List<CandidaturaDoCandidato> itens, long totalItens) {
        this.itens = Collections.unmodifiableList(new ArrayList<>(itens));
        this.totalItens = totalItens;
    }

    public List<CandidaturaDoCandidato> getItens() { return itens; }
    public long getTotalItens() { return totalItens; }
}
