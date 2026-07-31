package br.com.pacto.recrutamento.app.ports.out.usuario;

import br.com.pacto.recrutamento.core.entities.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioPort {
    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorId(UUID id);

    Usuario salvar(Usuario usuario);
}
