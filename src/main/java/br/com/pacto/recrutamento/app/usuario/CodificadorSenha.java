package br.com.pacto.recrutamento.app.usuario;

public interface CodificadorSenha {
    String codificar(String senha);
    boolean confere(String senha, String hash);
}
