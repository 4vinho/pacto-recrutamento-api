package br.com.pacto.recrutamento.app.dtos.usuario;

import java.util.UUID;

public class UsuarioDTO {
    private final UUID id;
    private final String email;
    private final String telefone;

    public UsuarioDTO(UUID id, String email, String telefone) {
        this.id = id;
        this.email = email;
        this.telefone = telefone;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
}
