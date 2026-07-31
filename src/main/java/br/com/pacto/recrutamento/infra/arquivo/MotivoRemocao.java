package br.com.pacto.recrutamento.infra.arquivo;

final class MotivoRemocao {
    private static final int TAMANHO_MAXIMO = 500;

    private MotivoRemocao() {
    }

    static String sanitizar(String motivo) {
        String seguro = motivo == null ? "Falha não informada" : motivo;
        seguro = seguro.replace('\r', ' ').replace('\n', ' ').trim();
        return seguro.length() <= TAMANHO_MAXIMO
                ? seguro
                : seguro.substring(0, TAMANHO_MAXIMO);
    }
}
