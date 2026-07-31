package br.com.pacto.recrutamento.app.dtos.usuario;

public class AutenticarUsuarioDTO {
    private final String email;
    private final String senha;

    public AutenticarUsuarioDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
