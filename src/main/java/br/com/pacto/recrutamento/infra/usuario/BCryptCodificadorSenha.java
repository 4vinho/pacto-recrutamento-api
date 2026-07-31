package br.com.pacto.recrutamento.infra.usuario;

import br.com.pacto.recrutamento.app.ports.usuario.CodificadorSenha;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptCodificadorSenha implements CodificadorSenha {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public String codificar(String senha) { return encoder.encode(senha); }
    public boolean confere(String senha, String hash) { return encoder.matches(senha, hash); }
}
