package br.com.pacto.recrutamento.infra.usuario;

import br.com.pacto.recrutamento.app.ports.usuario.UsuarioPort;
import br.com.pacto.recrutamento.core.entities.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UsuarioJpaAdapter implements UsuarioPort {
    private final UsuarioJpaRepository repository;

    public UsuarioJpaAdapter(UsuarioJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }
}
