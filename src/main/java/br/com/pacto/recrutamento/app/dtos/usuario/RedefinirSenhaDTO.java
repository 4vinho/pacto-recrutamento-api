package br.com.pacto.recrutamento.app.dtos.usuario;

public class RedefinirSenhaDTO {
    private final String token;
    private final String novaSenha;

    public RedefinirSenhaDTO(String token, String novaSenha) {
        this.token = token;
        this.novaSenha = novaSenha;
    }

    public String getToken() {
        return token;
    }

    public String getNovaSenha() {
        return novaSenha;
    }
}
