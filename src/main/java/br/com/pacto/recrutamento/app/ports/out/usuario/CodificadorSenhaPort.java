package br.com.pacto.recrutamento.app.ports.out.usuario;

public interface CodificadorSenhaPort {
    String codificar(String senha);

    boolean confere(String senha, String hash);
}
