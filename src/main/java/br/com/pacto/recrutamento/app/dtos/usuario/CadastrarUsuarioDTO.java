package br.com.pacto.recrutamento.app.dtos.usuario;

public class CadastrarUsuarioDTO {
    private final String nome;
    private final String email;
    private final String telefone;
    private final String senha;

    public CadastrarUsuarioDTO(String email, String telefone, String senha) {
        this(null, email, telefone, senha);
    }

    public CadastrarUsuarioDTO(String nome, String email, String telefone, String senha) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
    }
    public String getNome() { return nome; }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getSenha() {
        return senha;
    }
}
