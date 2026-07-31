package br.com.pacto.recrutamento.app.dtos.usuario;

public class CadastrarUsuarioDTO {
    private final String email;
    private final String telefone;
    private final String senha;

    public CadastrarUsuarioDTO(String email, String telefone, String senha) {
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
    }

    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getSenha() { return senha; }
}
