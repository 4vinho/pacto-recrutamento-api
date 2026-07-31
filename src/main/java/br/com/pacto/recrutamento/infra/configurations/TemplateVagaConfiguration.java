package br.com.pacto.recrutamento.infra.configurations;

import br.com.pacto.recrutamento.infra.repositorys.templatevaga.AutorizacaoTemplateVagaJpaRepository;

import br.com.pacto.recrutamento.app.ports.templatevaga.AutorizacaoTemplateVaga;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TemplateVagaConfiguration {
    @Bean
    AutorizacaoTemplateVaga autorizacaoTemplateVaga(
            AutorizacaoTemplateVagaJpaRepository repository) {
        return usuarioId -> usuarioId != null
                && repository.administradorAtivo(usuarioId, NomePapel.ADMINISTRADOR);
    }
}
