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
            @Override
            public boolean podeManterTemplates(UUID usuarioId) {
                return usuarioId != null && repository.possuiPapelAtivo(usuarioId,
                        java.util.Arrays.asList(NomePapel.ADMINISTRADOR, NomePapel.RESPONSAVEL_VAGA));
            }

            @Override
            public boolean podeExcluirTemplates(UUID usuarioId) {
                return usuarioId != null
                        && repository.administradorAtivo(usuarioId, NomePapel.ADMINISTRADOR);
            }
        };
    }
}
