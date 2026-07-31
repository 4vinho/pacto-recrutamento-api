package br.com.pacto.recrutamento.app.curriculo;

import java.time.Duration;

public interface ArquivoStorage {
    void armazenar(String chave, byte[] conteudo, String contentType);
    void remover(String chave);
    String gerarUrlTemporaria(String chave, Duration duracao);
}
