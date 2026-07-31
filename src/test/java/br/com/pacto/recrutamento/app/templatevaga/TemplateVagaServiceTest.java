package br.com.pacto.recrutamento.app.ports.out.templatevaga;

import br.com.pacto.recrutamento.app.dtos.templatevaga.CriarTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.CriarVagaAPartirDoTemplateDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.ExcluirItemTemplateVagaDTO;
import br.com.pacto.recrutamento.app.dtos.templatevaga.SalvarPerguntaTemplateVagaDTO;
import br.com.pacto.recrutamento.app.usecases.templatevaga.TemplateVagaService;
import br.com.pacto.recrutamento.core.entities.*;
import br.com.pacto.recrutamento.core.enums.TipoResposta;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TemplateVagaServiceTest {
    @Test
    void recusa_criacao_para_nao_administrador() {
        Memoria memoria = new Memoria();
        TemplateVagaService service = memoria.service(false);
        assertEquals(403, service.criarTemplate(new CriarTemplateVagaDTO(UUID.randomUUID(), "Titulo", "Descricao")).getStatusCode());
    }

    @Test
    void recusa_alteracao_de_pergunta_de_outro_template() {
        Memoria memoria = new Memoria();
        TemplateVaga template = memoria.template();
        PerguntaTemplateVaga pergunta = new PerguntaTemplateVaga();
        pergunta.setTemplateVagaId(UUID.randomUUID());
        memoria.perguntas.salvar(pergunta);
        SalvarPerguntaTemplateVagaDTO command = new SalvarPerguntaTemplateVagaDTO(UUID.randomUUID(), template.getId(), pergunta.getId(), "Questao", TipoResposta.TEXTO, true, 1);
        assertEquals(404, memoria.service(true).atualizarPerguntaDoTemplate(command).getStatusCode());
    }

    @Test
    void exclui_template_logicamente() {
        Memoria memoria = new Memoria();
        TemplateVaga template = memoria.template();
        assertEquals(204, memoria.service(true).excluirTemplate(new ExcluirItemTemplateVagaDTO(UUID.randomUUID(), template.getId(), null)).getStatusCode());
        assertNotNull(template.getExcluidoEm());
        assertFalse(memoria.templates.buscarAtivoPorId(template.getId()).isPresent());
    }

    @Test
    void copia_template_para_vaga_com_itens_novos_e_independentes() {
        Memoria memoria = new Memoria();
        TemplateVaga template = memoria.template();
        PerguntaTemplateVaga pergunta = new PerguntaTemplateVaga();
        pergunta.setTemplateVagaId(template.getId());
        pergunta.setEnunciado("Questao");
        pergunta.setTipoResposta(TipoResposta.TEXTO);
        pergunta.setOrdem(1);
        memoria.perguntas.salvar(pergunta);
        RequisitoTemplateVaga requisito = new RequisitoTemplateVaga();
        requisito.setTemplateVagaId(template.getId());
        requisito.setDescricao("Java");
        memoria.requisitos.salvar(requisito);
        assertEquals(201, memoria.service(true).criarVagaAPartirDoTemplate(new CriarVagaAPartirDoTemplateDTO(UUID.randomUUID(), template.getId())).getStatusCode());
        assertEquals(1, memoria.vagas.valores.size());
        assertEquals(1, memoria.perguntasVaga.valores.size());
        assertEquals(1, memoria.requisitosVaga.valores.size());
        assertNotEquals(pergunta.getId(), memoria.perguntasVaga.valores.values().iterator().next().getId());
    }

    static class Memoria {
        final Templates templates = new Templates();
        final Perguntas perguntas = new Perguntas();
        final Requisitos requisitos = new Requisitos();
        final Vagas vagas = new Vagas();
        final PerguntasVaga perguntasVaga = new PerguntasVaga();
        final RequisitosVaga requisitosVaga = new RequisitosVaga();

        TemplateVagaService service(boolean admin) {
            return new TemplateVagaService(templates, perguntas, requisitos, vagas, perguntasVaga, requisitosVaga, id -> admin, Clock.systemUTC());
        }

        TemplateVaga template() {
            TemplateVaga t = new TemplateVaga(UUID.randomUUID(), "Titulo", "Descricao");
            return templates.salvar(t);
        }
    }

    static class Templates implements TemplateVagaPort {
        Map<UUID, TemplateVaga> valores = new HashMap<>();

        public Optional<TemplateVaga> buscarAtivoPorId(UUID id) {
            return Optional.ofNullable(valores.get(id)).filter(x -> x.getExcluidoEm() == null);
        }

        public TemplateVaga salvar(TemplateVaga x) {
            valores.put(x.getId(), x);
            return x;
        }
    }

    static class Perguntas implements PerguntaTemplateVagaPort {
        Map<UUID, PerguntaTemplateVaga> valores = new HashMap<>();

        public Optional<PerguntaTemplateVaga> buscarAtivaPorId(UUID id) {
            return Optional.ofNullable(valores.get(id)).filter(x -> x.getExcluidoEm() == null);
        }

        public List<PerguntaTemplateVaga> listarAtivasDoTemplate(UUID id) {
            List<PerguntaTemplateVaga> r = new ArrayList<>();
            for (PerguntaTemplateVaga x : valores.values())
                if (id.equals(x.getTemplateVagaId()) && x.getExcluidoEm() == null) r.add(x);
            return r;
        }

        public PerguntaTemplateVaga salvar(PerguntaTemplateVaga x) {
            valores.put(x.getId(), x);
            return x;
        }
    }

    static class Requisitos implements RequisitoTemplateVagaPort {
        Map<UUID, RequisitoTemplateVaga> valores = new HashMap<>();

        public Optional<RequisitoTemplateVaga> buscarAtivoPorId(UUID id) {
            return Optional.ofNullable(valores.get(id)).filter(x -> x.getExcluidoEm() == null);
        }

        public List<RequisitoTemplateVaga> listarAtivosDoTemplate(UUID id) {
            List<RequisitoTemplateVaga> r = new ArrayList<>();
            for (RequisitoTemplateVaga x : valores.values())
                if (id.equals(x.getTemplateVagaId()) && x.getExcluidoEm() == null) r.add(x);
            return r;
        }

        public RequisitoTemplateVaga salvar(RequisitoTemplateVaga x) {
            valores.put(x.getId(), x);
            return x;
        }
    }

    static class Vagas implements VagaTemplatePort {
        Map<UUID, Vaga> valores = new HashMap<>();

        public Vaga salvar(Vaga x) {
            valores.put(x.getId(), x);
            return x;
        }
    }

    static class PerguntasVaga implements PerguntaVagaTemplatePort {
        Map<UUID, PerguntaVaga> valores = new HashMap<>();

        public PerguntaVaga salvar(PerguntaVaga x) {
            valores.put(x.getId(), x);
            return x;
        }
    }

    static class RequisitosVaga implements RequisitoVagaTemplatePort {
        Map<UUID, RequisitoVaga> valores = new HashMap<>();

        public RequisitoVaga salvar(RequisitoVaga x) {
            valores.put(x.getId(), x);
            return x;
        }
    }
}
