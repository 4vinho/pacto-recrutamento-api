package br.com.pacto.recrutamento.core.common;

import java.util.UUID;

public class FiltroRespostaCandidatura {
    public enum Operador {
        CONTEM, NAO_CONTEM, IGUAL, DIFERENTE,
        MAIOR_QUE, MAIOR_OU_IGUAL, MENOR_QUE, MENOR_OU_IGUAL, ANTES_DE, DEPOIS_DE
    }

    private final UUID perguntaId;
    private final Operador operador;
    private final String valor;

    public FiltroRespostaCandidatura(UUID perguntaId, Operador operador, String valor) {
        this.perguntaId = perguntaId;
        this.operador = operador;
        this.valor = valor;
    }

    public UUID getPerguntaId() { return perguntaId; }
    public Operador getOperador() { return operador; }
    public String getValor() { return valor; }
}
