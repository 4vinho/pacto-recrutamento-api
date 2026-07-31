package br.com.pacto.recrutamento.app.dtos.usuario;

public class SolicitarRecuperacaoSenhaDTO {
    private final String email;

    public SolicitarRecuperacaoSenhaDTO(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
}
