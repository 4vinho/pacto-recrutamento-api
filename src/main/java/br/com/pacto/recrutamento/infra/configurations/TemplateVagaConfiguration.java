package br.com.pacto.recrutamento.infra.configurations;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.AutorizacaoTemplateVagaPort;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import br.com.pacto.recrutamento.infra.repositorys.templatevaga.AutorizacaoTemplateVagaJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TemplateVagaConfiguration {
    @Bean
    AutorizacaoTemplateVagaPort autorizacaoTemplateVaga(
            AutorizacaoTemplateVagaJpaRepository repository) {
        return new AutorizacaoTemplateVagaPort() {
            public boolean podeManterTemplates(java.util.UUID usuarioId) {
                return usuarioId != null
                        && repository.administradorAtivo(usuarioId, NomePapel.ADMINISTRADOR);
            }

            public boolean podeConsultarTemplates(java.util.UUID usuarioId) {
                return usuarioId != null && repository.consultorAtivo(usuarioId);
            }
        };
    }
}
