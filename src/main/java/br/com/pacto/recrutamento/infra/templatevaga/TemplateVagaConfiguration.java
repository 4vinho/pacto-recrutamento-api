package br.com.pacto.recrutamento.infra.templatevaga;

import br.com.pacto.recrutamento.app.services.TemplateVagaService;
import br.com.pacto.recrutamento.app.dtos.templatevaga.AtualizarTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.CriarTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.CriarVagaAPartirDoTemplateDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.ExcluirItemTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.PerguntaTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.RequisitoTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.SalvarPerguntaTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.SalvarRequisitoTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.TemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.vaga.VagaDTO;
import br.com.pacto.recrutamento.app.templatevaga.AutorizacaoTemplateVaga;
import br.com.pacto.recrutamento.app.templatevaga.PerguntaTemplateVagaRepositorio;
import br.com.pacto.recrutamento.app.templatevaga.PerguntaVagaTemplateRepositorio;
import br.com.pacto.recrutamento.app.templatevaga.RequisitoTemplateVagaRepositorio;
import br.com.pacto.recrutamento.app.templatevaga.RequisitoVagaTemplateRepositorio;
import br.com.pacto.recrutamento.app.templatevaga.TemplateVagaRepositorio;
import br.com.pacto.recrutamento.app.templatevaga.TemplateVagaServiceImpl;
import br.com.pacto.recrutamento.app.templatevaga.VagaTemplateRepositorio;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import java.time.Clock;

@Configuration
class TemplateVagaConfiguration {
    @Bean
    AutorizacaoTemplateVaga autorizacaoTemplateVaga(AutorizacaoTemplateVagaJpaRepository repository) {
        return usuarioId -> usuarioId != null && repository.administradorAtivo(usuarioId, NomePapel.ADMINISTRADOR);
    }

    @Bean
    TemplateVagaService templateVagaService(TemplateVagaRepositorio templates,
                                            PerguntaTemplateVagaRepositorio perguntas,
                                            RequisitoTemplateVagaRepositorio requisitos,
                                            VagaTemplateRepositorio vagas,
                                            PerguntaVagaTemplateRepositorio perguntasVaga,
                                            RequisitoVagaTemplateRepositorio requisitosVaga,
                                            AutorizacaoTemplateVaga autorizacao) {
        TemplateVagaService service = new TemplateVagaServiceImpl(templates, perguntas, requisitos, vagas, perguntasVaga,
                requisitosVaga, autorizacao, Clock.systemUTC());
        return new Transacional(service);
    }

    static class Transacional implements TemplateVagaService {
        private final TemplateVagaService delegate;
        Transacional(TemplateVagaService delegate) { this.delegate = delegate; }
        public TypedResponse<TemplateVagaDTO> criarTemplate(CriarTemplateVagaDTO command) { return delegate.criarTemplate(command); }
        public TypedResponse<TemplateVagaDTO> atualizarTemplate(AtualizarTemplateVagaDTO command) { return delegate.atualizarTemplate(command); }
        public TypedResponse<Void> excluirTemplate(ExcluirItemTemplateVagaDTO command) { return delegate.excluirTemplate(command); }
        public TypedResponse<PerguntaTemplateVagaDTO> criarPerguntaDoTemplate(SalvarPerguntaTemplateVagaDTO command) { return delegate.criarPerguntaDoTemplate(command); }
        public TypedResponse<PerguntaTemplateVagaDTO> atualizarPerguntaDoTemplate(SalvarPerguntaTemplateVagaDTO command) { return delegate.atualizarPerguntaDoTemplate(command); }
        public TypedResponse<Void> excluirPerguntaDoTemplate(ExcluirItemTemplateVagaDTO command) { return delegate.excluirPerguntaDoTemplate(command); }
        public TypedResponse<RequisitoTemplateVagaDTO> criarRequisitoDoTemplate(SalvarRequisitoTemplateVagaDTO command) { return delegate.criarRequisitoDoTemplate(command); }
        public TypedResponse<RequisitoTemplateVagaDTO> atualizarRequisitoDoTemplate(SalvarRequisitoTemplateVagaDTO command) { return delegate.atualizarRequisitoDoTemplate(command); }
        public TypedResponse<Void> excluirRequisitoDoTemplate(ExcluirItemTemplateVagaDTO command) { return delegate.excluirRequisitoDoTemplate(command); }
        @Transactional
        public TypedResponse<VagaDTO> criarVagaAPartirDoTemplate(CriarVagaAPartirDoTemplateDTO command) { return delegate.criarVagaAPartirDoTemplate(command); }
    }
}
