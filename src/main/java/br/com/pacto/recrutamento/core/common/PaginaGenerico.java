package br.com.pacto.recrutamento.core.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaginaGenerico<T> {
    private final List<T> itens;
    private final long totalItens;

    public PaginaGenerico(List<T> itens, long totalItens) {
        if (itens == null) {
            throw new IllegalArgumentException("Os itens da página são obrigatórios");
        }
        if (totalItens < 0) {
            throw new IllegalArgumentException("O total de itens não pode ser negativo");
        }
        this.itens = Collections.unmodifiableList(new ArrayList<>(itens));
        this.totalItens = totalItens;
    }

    public List<T> getItens() {
        return itens;
    }

    public long getTotalItens() {
        return totalItens;
    }
}
