package br.com.pacto.recrutamento.infra.adapters.usuario;

import br.com.pacto.recrutamento.app.ports.out.usuario.CanalRecuperacaoSenhaPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class CanalRecuperacaoSenhaSimulado implements CanalRecuperacaoSenhaPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            CanalRecuperacaoSenhaSimulado.class);

    @Override
    public void enviar(String email, String token, OffsetDateTime expiraEm) {
        LOGGER.info("[SIMULADO] Recuperacao de senha: email={}, token={}, expiraEm={}",
                email, token, expiraEm);
    }
}
