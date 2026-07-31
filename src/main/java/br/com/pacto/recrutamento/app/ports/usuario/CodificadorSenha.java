package br.com.pacto.recrutamento.app.ports.usuario;

public interface CodificadorSenha {
    String codificar(String senha);

    boolean confere(String senha, String hash);
}
