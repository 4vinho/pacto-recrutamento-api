package br.com.pacto.recrutamento.app.serviceImpl;

import br.com.pacto.recrutamento.app.dtos.templatevaga.*;
import br.com.pacto.recrutamento.app.dtos.vaga.VagaDTO;
import br.com.pacto.recrutamento.app.ports.templatevaga.*;
import br.com.pacto.recrutamento.app.services.TemplateVagaService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TemplateVagaServiceImpl implements TemplateVagaService {
    private final TemplateVagaRepositorio templates;
    private final PerguntaTemplateVagaRepositorio perguntas;
    private final RequisitoTemplateVagaRepositorio requisitos;
    private final VagaTemplateRepositorio vagas;
    private final PerguntaVagaTemplateRepositorio perguntasVaga;
    private final RequisitoVagaTemplateRepositorio requisitosVaga;
    private final AutorizacaoTemplateVaga autorizacao;
    private final Clock clock;

    public TemplateVagaServiceImpl(TemplateVagaRepositorio templates,
                                   PerguntaTemplateVagaRepositorio perguntas,
                                   RequisitoTemplateVagaRepositorio requisitos,
                                   VagaTemplateRepositorio vagas,
                                   PerguntaVagaTemplateRepositorio perguntasVaga,
                                   RequisitoVagaTemplateRepositorio requisitosVaga,
                                   AutorizacaoTemplateVaga autorizacao,
                                   Clock clock) {
        this.templates = templates;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.vagas = vagas;
        this.perguntasVaga = perguntasVaga;
        this.requisitosVaga = requisitosVaga;
        this.autorizacao = autorizacao;
        this.clock = clock;
    }

    @Override
    public TypedResponse<TemplateVagaDTO> criarTemplate(CriarTemplateVagaDTO command) {
        if (command == null || command.getResponsavelId() == null
                || camposTemplateInvalidos(command.getTitulo(), command.getDescricao())) {
            return erro(400, "Dados do template invalidos");
        }
        if (!administrador(command.getResponsavelId())) return erro(403, "Acesso nao autorizado");
        TemplateVaga template = new TemplateVaga(command.getResponsavelId(), command.getTitulo(), command.getDescricao());
        return respostaTemplate(201, "Template criado", templates.salvar(template));
    }

    @Override
    public TypedResponse<TemplateVagaDTO> atualizarTemplate(AtualizarTemplateVagaDTO command) {
        if (command == null || command.getTemplateId() == null
                || camposTemplateInvalidos(command.getTitulo(), command.getDescricao())) {
            return erro(400, "Dados do template invalidos");
        }
        if (!administrador(command.getUsuarioSolicitanteId())) return erro(403, "Acesso nao autorizado");
        Optional<TemplateVaga> template = templates.buscarAtivoPorId(command.getTemplateId());
        if (!template.isPresent()) return erro(404, "Template nao encontrado");
        template.get().setTitulo(command.getTitulo());
        template.get().setDescricao(command.getDescricao());
        return respostaTemplate(200, "Template atualizado", templates.salvar(template.get()));
    }

    @Override
    public TypedResponse<Void> excluirTemplate(ExcluirItemTemplateVagaDTO command) {
        if (command == null || command.getTemplateId() == null) return vazio(400, "Dados do template invalidos");
        if (!administrador(command.getUsuarioSolicitanteId())) return vazio(403, "Acesso nao autorizado");
        Optional<TemplateVaga> template = templates.buscarAtivoPorId(command.getTemplateId());
        if (!template.isPresent()) return vazio(404, "Template nao encontrado");
        template.get().setExcluidoEm(agora());
        templates.salvar(template.get());
        return vazio(204, "Template excluido");
    }

    @Override
    public TypedResponse<PerguntaTemplateVagaDTO> criarPerguntaDoTemplate(SalvarPerguntaTemplateVagaDTO command) {
        if (command == null || perguntaInvalida(command)) return erro(400, "Dados da pergunta invalidos");
        if (!administrador(command.getUsuarioSolicitanteId())) return erro(403, "Acesso nao autorizado");
        if (!templateExiste(command.getTemplateId())) return erro(404, "Template nao encontrado");
        PerguntaTemplateVaga pergunta = new PerguntaTemplateVaga();
        preencher(pergunta, command);
        return respostaPergunta(201, "Pergunta criada", perguntas.salvar(pergunta));
    }

    @Override
    public TypedResponse<PerguntaTemplateVagaDTO> atualizarPerguntaDoTemplate(SalvarPerguntaTemplateVagaDTO command) {
        if (command == null || command.getPerguntaId() == null || perguntaInvalida(command))
            return erro(400, "Dados da pergunta invalidos");
        if (!administrador(command.getUsuarioSolicitanteId())) return erro(403, "Acesso nao autorizado");
        if (!templateExiste(command.getTemplateId())) return erro(404, "Template nao encontrado");
        Optional<PerguntaTemplateVaga> pergunta = perguntas.buscarAtivaPorId(command.getPerguntaId());
        if (!perguntaPertenceAoTemplate(pergunta, command.getTemplateId())) return erro(404, "Pergunta nao encontrada");
        preencher(pergunta.get(), command);
        return respostaPergunta(200, "Pergunta atualizada", perguntas.salvar(pergunta.get()));
    }

    @Override
    public TypedResponse<Void> excluirPerguntaDoTemplate(ExcluirItemTemplateVagaDTO command) {
        if (itemInvalido(command)) return vazio(400, "Dados da pergunta invalidos");
        if (!administrador(command.getUsuarioSolicitanteId())) return vazio(403, "Acesso nao autorizado");
        if (!templateExiste(command.getTemplateId())) return vazio(404, "Template nao encontrado");
        Optional<PerguntaTemplateVaga> pergunta = perguntas.buscarAtivaPorId(command.getItemId());
        if (!perguntaPertenceAoTemplate(pergunta, command.getTemplateId()))
            return vazio(404, "Pergunta nao encontrada");
        pergunta.get().setExcluidoEm(agora());
        perguntas.salvar(pergunta.get());
        return vazio(204, "Pergunta excluida");
    }

    @Override
    public TypedResponse<RequisitoTemplateVagaDTO> criarRequisitoDoTemplate(SalvarRequisitoTemplateVagaDTO command) {
        if (command == null || requisitoInvalido(command)) return erro(400, "Dados do requisito invalidos");
        if (!administrador(command.getUsuarioSolicitanteId())) return erro(403, "Acesso nao autorizado");
        if (!templateExiste(command.getTemplateId())) return erro(404, "Template nao encontrado");
        RequisitoTemplateVaga requisito = new RequisitoTemplateVaga();
        preencher(requisito, command);
        return respostaRequisito(201, "Requisito criado", requisitos.salvar(requisito));
    }

    @Override
    public TypedResponse<RequisitoTemplateVagaDTO> atualizarRequisitoDoTemplate(SalvarRequisitoTemplateVagaDTO command) {
        if (command == null || command.getRequisitoId() == null || requisitoInvalido(command))
            return erro(400, "Dados do requisito invalidos");
        if (!administrador(command.getUsuarioSolicitanteId())) return erro(403, "Acesso nao autorizado");
        if (!templateExiste(command.getTemplateId())) return erro(404, "Template nao encontrado");
        Optional<RequisitoTemplateVaga> requisito = requisitos.buscarAtivoPorId(command.getRequisitoId());
        if (!requisitoPertenceAoTemplate(requisito, command.getTemplateId()))
            return erro(404, "Requisito nao encontrado");
        preencher(requisito.get(), command);
        return respostaRequisito(200, "Requisito atualizado", requisitos.salvar(requisito.get()));
    }

    @Override
    public TypedResponse<Void> excluirRequisitoDoTemplate(ExcluirItemTemplateVagaDTO command) {
        if (itemInvalido(command)) return vazio(400, "Dados do requisito invalidos");
        if (!administrador(command.getUsuarioSolicitanteId())) return vazio(403, "Acesso nao autorizado");
        if (!templateExiste(command.getTemplateId())) return vazio(404, "Template nao encontrado");
        Optional<RequisitoTemplateVaga> requisito = requisitos.buscarAtivoPorId(command.getItemId());
        if (!requisitoPertenceAoTemplate(requisito, command.getTemplateId()))
            return vazio(404, "Requisito nao encontrado");
        requisito.get().setExcluidoEm(agora());
        requisitos.salvar(requisito.get());
        return vazio(204, "Requisito excluido");
    }

    @Override
    @Transactional
    public TypedResponse<VagaDTO> criarVagaAPartirDoTemplate(CriarVagaAPartirDoTemplateDTO command) {
        if (command == null || command.getTemplateId() == null) return erro(400, "Dados do template invalidos");
        if (!administrador(command.getUsuarioSolicitanteId())) return erro(403, "Acesso nao autorizado");
        Optional<TemplateVaga> template = templates.buscarAtivoPorId(command.getTemplateId());
        if (!template.isPresent()) return erro(404, "Template nao encontrado");
        Vaga vaga = vagas.salvar(criarVaga(template.get()));
        copiarPerguntas(template.get().getId(), vaga.getId());
        copiarRequisitos(template.get().getId(), vaga.getId());
        return respostaVaga(vaga);
    }

    private Vaga criarVaga(TemplateVaga template) {
        return new Vaga(template.getResponsavelId(), template.getTitulo(), template.getDescricao());
    }

    private void copiarPerguntas(UUID templateId, UUID vagaId) {
        for (PerguntaTemplateVaga origem : perguntas.listarAtivasDoTemplate(templateId))
            perguntasVaga.salvar(copiar(origem, vagaId));
    }

    private void copiarRequisitos(UUID templateId, UUID vagaId) {
        for (RequisitoTemplateVaga origem : requisitos.listarAtivosDoTemplate(templateId))
            requisitosVaga.salvar(copiar(origem, vagaId));
    }

    private PerguntaVaga copiar(PerguntaTemplateVaga origem, UUID vagaId) {
        PerguntaVaga destino = new PerguntaVaga();
        destino.setVagaId(vagaId);
        destino.setEnunciado(origem.getEnunciado());
        destino.setTipoResposta(origem.getTipoResposta());
        destino.setObrigatoria(origem.isObrigatoria());
        destino.setOrdem(origem.getOrdem());
        return destino;
    }

    private RequisitoVaga copiar(RequisitoTemplateVaga origem, UUID vagaId) {
        RequisitoVaga destino = new RequisitoVaga();
        destino.setVagaId(vagaId);
        destino.setDescricao(origem.getDescricao());
        destino.setObrigatorio(origem.isObrigatorio());
        return destino;
    }

    private boolean templateExiste(UUID id) {
        return templates.buscarAtivoPorId(id).isPresent();
    }

    private boolean administrador(UUID id) {
        return id != null && autorizacao.podeManterTemplates(id);
    }

    private boolean camposTemplateInvalidos(String titulo, String descricao) {
        return emBranco(titulo) || emBranco(descricao);
    }

    private boolean perguntaInvalida(SalvarPerguntaTemplateVagaDTO command) {
        return command.getTemplateId() == null || emBranco(command.getEnunciado()) || command.getTipoResposta() == null || command.getOrdem() <= 0;
    }

    private boolean requisitoInvalido(SalvarRequisitoTemplateVagaDTO command) {
        return command.getTemplateId() == null || emBranco(command.getDescricao());
    }

    private boolean itemInvalido(ExcluirItemTemplateVagaDTO command) {
        return command == null || command.getTemplateId() == null || command.getItemId() == null;
    }

    private boolean emBranco(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private boolean perguntaPertenceAoTemplate(Optional<PerguntaTemplateVaga> pergunta, UUID templateId) {
        return pergunta.isPresent() && templateId.equals(pergunta.get().getTemplateVagaId());
    }

    private boolean requisitoPertenceAoTemplate(Optional<RequisitoTemplateVaga> requisito, UUID templateId) {
        return requisito.isPresent() && templateId.equals(requisito.get().getTemplateVagaId());
    }

    private OffsetDateTime agora() {
        return OffsetDateTime.now(clock);
    }

    private void preencher(PerguntaTemplateVaga pergunta, SalvarPerguntaTemplateVagaDTO command) {
        pergunta.setTemplateVagaId(command.getTemplateId());
        pergunta.setEnunciado(command.getEnunciado());
        pergunta.setTipoResposta(command.getTipoResposta());
        pergunta.setObrigatoria(command.isObrigatoria());
        pergunta.setOrdem(command.getOrdem());
    }

    private void preencher(RequisitoTemplateVaga requisito, SalvarRequisitoTemplateVagaDTO command) {
        requisito.setTemplateVagaId(command.getTemplateId());
        requisito.setDescricao(command.getDescricao());
        requisito.setObrigatorio(command.isObrigatorio());
    }

    private TypedResponse<TemplateVagaDTO> respostaTemplate(int status, String mensagem, TemplateVaga template) {
        return new TypedResponse<TemplateVagaDTO>(status, mensagem, new TemplateVagaDTO(template.getId(), template.getResponsavelId(), template.getTitulo(), template.getDescricao()));
    }

    private TypedResponse<PerguntaTemplateVagaDTO> respostaPergunta(int status, String mensagem, PerguntaTemplateVaga pergunta) {
        return new TypedResponse<PerguntaTemplateVagaDTO>(status, mensagem, new PerguntaTemplateVagaDTO(pergunta.getId(), pergunta.getEnunciado(), pergunta.getTipoResposta(), pergunta.isObrigatoria(), pergunta.getOrdem()));
    }

    private TypedResponse<RequisitoTemplateVagaDTO> respostaRequisito(int status, String mensagem, RequisitoTemplateVaga requisito) {
        return new TypedResponse<RequisitoTemplateVagaDTO>(status, mensagem, new RequisitoTemplateVagaDTO(requisito.getId(), requisito.getDescricao(), requisito.isObrigatorio()));
    }

    private TypedResponse<VagaDTO> respostaVaga(Vaga vaga) {
        return new TypedResponse<VagaDTO>(201, "Vaga criada a partir do template", new VagaDTO(vaga.getId(), vaga.getResponsavelId(), vaga.getTitulo(), vaga.getDescricao(), vaga.getStatus()));
    }

    private <T> TypedResponse<T> erro(int status, String mensagem) {
        return new TypedResponse<T>(status, mensagem, null);
    }

    private TypedResponse<Void> vazio(int status, String mensagem) {
        return new TypedResponse<Void>(status, mensagem, null);
    }
}
