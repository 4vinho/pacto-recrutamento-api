package br.com.pacto.recrutamento.app.ports.out.curriculo;

import java.time.Duration;

public interface ArquivoStoragePort {
    void armazenar(String chave, byte[] conteudo, String contentType);

    void remover(String chave);

    String gerarUrlTemporaria(String chave, Duration duracao);
}
