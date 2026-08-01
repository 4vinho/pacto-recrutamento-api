package br.com.pacto.recrutamento.app.usecases.vaga;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.app.ports.in.vaga.VagaUseCase;
import br.com.pacto.recrutamento.app.ports.out.vaga.AutorizacaoVagaPort;
import br.com.pacto.recrutamento.app.ports.out.vaga.PerguntaVagaPort;
import br.com.pacto.recrutamento.app.ports.out.vaga.RequisitoVagaPort;
import br.com.pacto.recrutamento.app.ports.out.vaga.VagaPort;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.enums.StatusVaga;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VagaService implements VagaUseCase {
    private final VagaPort vagas;
    private final PerguntaVagaPort perguntas;
    private final RequisitoVagaPort requisitos;
    private final AutorizacaoVagaPort autorizacao;
    private final Clock clock;

    public VagaService(VagaPort vagas, PerguntaVagaPort perguntas,
                       RequisitoVagaPort requisitos, AutorizacaoVagaPort autorizacao,
                       Clock clock) {
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.autorizacao = autorizacao;
        this.clock = clock;
    }

    @Override
    public TypedPagedResponse<VagaDTO> listarVagas(ListarVagasDTO query) {
        if (query == null || query.getUsuarioId() == null || query.getPage() < 0
                || query.getPageSize() <= 0 || query.getPageSize() > 100
                || !ordenacaoValida(query.getOrdenarPor())) {
            int page = query == null || query.getPage() < 0 ? 0 : query.getPage();
            int size = query == null || query.getPageSize() <= 0 ? 20 : query.getPageSize();
            return new TypedPagedResponse<>(400, "Consulta de vagas invalida",
                    Collections.<VagaDTO>emptyList(), page, size, 0);
        }
        StatusVaga status = autorizado(query.getUsuarioId()) ? query.getStatus() : StatusVaga.PUBLICADA;
        PaginaGenerico<Vaga> pagina = vagas.listar(query.getBusca(), status, query.getPage(),
                query.getPageSize(), query.getOrdenarPor(), query.isAscendente());
        List<VagaDTO> dados = pagina.getItens().stream().map(this::paraDto).collect(Collectors.toList());
        return new TypedPagedResponse<>(200, "Vagas encontradas", dados, query.getPage(),
                query.getPageSize(), pagina.getTotalItens());
    }

    @Override
    public TypedResponse<VagaDetalheDTO> consultarVaga(ConsultarVagaDTO query) {
        if (query == null || query.getUsuarioId() == null || query.getVagaId() == null) {
            return new TypedResponse<>(400, "Consulta de vaga invalida", null);
        }
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(query.getVagaId());
        if (!vaga.isPresent() || (!autorizado(query.getUsuarioId())
                && vaga.get().getStatus() != StatusVaga.PUBLICADA)) {
            return new TypedResponse<>(404, VAGA_NAO_ENCONTRADA, null);
        }
        List<RequisitoVagaDTO> requisitosDto = requisitos.listarAtivosPorVagaId(query.getVagaId())
                .stream().map(r -> new RequisitoVagaDTO(r.getId(), r.getDescricao(), r.isObrigatorio()))
                .collect(Collectors.toList());
        List<PerguntaVagaDTO> perguntasDto = perguntas.listarAtivasPorVagaId(query.getVagaId())
                .stream().map(p -> new PerguntaVagaDTO(p.getId(), p.getEnunciado(),
                        p.getTipoResposta(), p.isObrigatoria(), p.getOrdem()))
                .collect(Collectors.toList());
        Vaga v = vaga.get();
        return new TypedResponse<>(200, "Vaga encontrada", new VagaDetalheDTO(v.getId(),
                v.getResponsaveisIds(), v.getTitulo(), v.getDescricao(), v.getStatus(),
                requisitosDto, perguntasDto));
    }

    @Override
    public TypedResponse<VagaDTO> criarVaga(CriarVagaDTO command) {
        if (command == null || responsaveisInvalidos(command.getResponsaveisIds())
                || camposVagaInvalidos(command.getTitulo(), command.getDescricao()))
            return vagaErro(400, DADOS_VAGA_INVALIDOS);
        if (!autorizacao.podeManterVagas(command.getUsuarioSolicitanteId())) return vagaErro(403, ACESSO_NAO_AUTORIZADO);
        if (!responsaveisAutorizados(command.getResponsaveisIds())) return vagaErro(422, RESPONSAVEL_VAGA_INVALIDO);
        return vagaResposta(201, "Vaga criada", vagas.salvar(new Vaga(command.getResponsaveisIds(), command.getTitulo(), command.getDescricao())));
    }

    @Override
    public TypedResponse<VagaDTO> atualizarVaga(AtualizarVagaDTO command) {
        if (command == null || responsaveisInvalidos(command.getResponsaveisIds())
                || camposVagaInvalidos(command.getTitulo(), command.getDescricao()))
            return vagaErro(400, DADOS_VAGA_INVALIDOS);
        TypedResponse<VagaDTO> acesso = validarAcesso(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        if (!responsaveisAutorizados(command.getResponsaveisIds())) return vagaErro(422, RESPONSAVEL_VAGA_INVALIDO);
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return vagaErro(404, VAGA_NAO_ENCONTRADA);
        vaga.get().setTitulo(command.getTitulo());
        vaga.get().setDescricao(command.getDescricao());
        vaga.get().setResponsaveisIds(command.getResponsaveisIds());
        return vagaResposta(200, "Vaga atualizada", vagas.salvar(vaga.get()));
    }

    @Override
    public TypedResponse<VagaDTO> alterarStatusVaga(AlterarStatusVagaDTO command) {
        if (command == null || command.getStatus() == null) return vagaErro(400, STATUS_VAGA_OBRIGATORIO);
        TypedResponse<VagaDTO> acesso = validarAcesso(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return vagaErro(404, VAGA_NAO_ENCONTRADA);
        try {
            vaga.get().setStatus(command.getStatus());
        } catch (IllegalStateException e) {
            return vagaErro(422, e.getMessage());
        }
        return vagaResposta(200, "Status da vaga atualizado", vagas.salvar(vaga.get()));
    }

    @Override
    public TypedResponse<Void> excluirVaga(ExcluirVagaDTO command) {
        if (command == null) return vazioErro(400, DADOS_VAGA_INVALIDOS);
        TypedResponse<Void> acesso = validarAcessoVazio(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return vazioErro(404, VAGA_NAO_ENCONTRADA);
        vaga.get().setExcluidoEm(agora());
        vagas.salvar(vaga.get());
        return new TypedResponse<Void>(204, "Vaga excluida", null);
    }

    @Override
    public TypedResponse<PerguntaVagaDTO> criarPerguntaDaVaga(SalvarPerguntaVagaDTO command) {
        if (command == null || perguntaInvalida(command)) return perguntaErro(400, DADOS_PERGUNTA_INVALIDOS);
        TypedResponse<PerguntaVagaDTO> acesso = validarAcessoPergunta(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        if (!vagas.buscarAtivaPorId(command.getVagaId()).isPresent()) return perguntaErro(404, VAGA_NAO_ENCONTRADA);
        PerguntaVaga pergunta = new PerguntaVaga();
        preencher(pergunta, command);
        return perguntaResposta(201, "Pergunta criada", perguntas.salvar(pergunta));
    }

    @Override
    public TypedResponse<PerguntaVagaDTO> atualizarPerguntaDaVaga(SalvarPerguntaVagaDTO command) {
        if (command == null || command.getPerguntaId() == null || perguntaInvalida(command))
            return perguntaErro(400, DADOS_PERGUNTA_INVALIDOS);
        TypedResponse<PerguntaVagaDTO> acesso = validarAcessoPergunta(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        if (!vagas.buscarAtivaPorId(command.getVagaId()).isPresent()) return perguntaErro(404, VAGA_NAO_ENCONTRADA);
        Optional<PerguntaVaga> pergunta = perguntas.buscarAtivaPorId(command.getPerguntaId());
        if (!pergunta.isPresent() || !command.getVagaId().equals(pergunta.get().getVagaId()))
            return perguntaErro(404, PERGUNTA_NAO_ENCONTRADA);
        preencher(pergunta.get(), command);
        return perguntaResposta(200, "Pergunta atualizada", perguntas.salvar(pergunta.get()));
    }

    @Override
    public TypedResponse<Void> excluirPerguntaDaVaga(ExcluirItemVagaDTO command) {
        if (command == null) return vazioErro(400, DADOS_PERGUNTA_INVALIDOS);
        TypedResponse<Void> acesso = validarAcessoVazio(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        if (!vagas.buscarAtivaPorId(command.getVagaId()).isPresent()) return vazioErro(404, VAGA_NAO_ENCONTRADA);
        Optional<PerguntaVaga> pergunta = perguntas.buscarAtivaPorId(command.getItemId());
        if (!pergunta.isPresent() || !command.getVagaId().equals(pergunta.get().getVagaId()))
            return vazioErro(404, PERGUNTA_NAO_ENCONTRADA);
        pergunta.get().setExcluidoEm(agora());
        perguntas.salvar(pergunta.get());
        return new TypedResponse<Void>(204, "Pergunta excluida", null);
    }

    @Override
    public TypedResponse<RequisitoVagaDTO> criarRequisitoDaVaga(SalvarRequisitoVagaDTO command) {
        if (command == null || requisitoInvalido(command)) return requisitoErro(400, DADOS_REQUISITO_INVALIDOS);
        TypedResponse<RequisitoVagaDTO> acesso = validarAcessoRequisito(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        if (!vagas.buscarAtivaPorId(command.getVagaId()).isPresent()) return requisitoErro(404, VAGA_NAO_ENCONTRADA);
        RequisitoVaga requisito = new RequisitoVaga();
        preencher(requisito, command);
        return requisitoResposta(201, "Requisito criado", requisitos.salvar(requisito));
    }

    @Override
    public TypedResponse<RequisitoVagaDTO> atualizarRequisitoDaVaga(SalvarRequisitoVagaDTO command) {
        if (command == null || command.getRequisitoId() == null || requisitoInvalido(command))
            return requisitoErro(400, DADOS_REQUISITO_INVALIDOS);
        TypedResponse<RequisitoVagaDTO> acesso = validarAcessoRequisito(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        if (!vagas.buscarAtivaPorId(command.getVagaId()).isPresent()) return requisitoErro(404, VAGA_NAO_ENCONTRADA);
        Optional<RequisitoVaga> requisito = requisitos.buscarAtivoPorId(command.getRequisitoId());
        if (!requisito.isPresent() || !command.getVagaId().equals(requisito.get().getVagaId()))
            return requisitoErro(404, REQUISITO_NAO_ENCONTRADO);
        preencher(requisito.get(), command);
        return requisitoResposta(200, "Requisito atualizado", requisitos.salvar(requisito.get()));
    }

    @Override
    public TypedResponse<Void> excluirRequisitoDaVaga(ExcluirItemVagaDTO command) {
        if (command == null) return vazioErro(400, DADOS_REQUISITO_INVALIDOS);
        TypedResponse<Void> acesso = validarAcessoVazio(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        if (!vagas.buscarAtivaPorId(command.getVagaId()).isPresent()) return vazioErro(404, VAGA_NAO_ENCONTRADA);
        Optional<RequisitoVaga> requisito = requisitos.buscarAtivoPorId(command.getItemId());
        if (!requisito.isPresent() || !command.getVagaId().equals(requisito.get().getVagaId()))
            return vazioErro(404, REQUISITO_NAO_ENCONTRADO);
        requisito.get().setExcluidoEm(agora());
        requisitos.salvar(requisito.get());
        return new TypedResponse<Void>(204, "Requisito excluido", null);
    }

    private boolean camposVagaInvalidos(String titulo, String descricao) {
        return emBranco(titulo) || emBranco(descricao);
    }

    private boolean perguntaInvalida(SalvarPerguntaVagaDTO command) {
        return command.getVagaId() == null || emBranco(command.getEnunciado()) || command.getTipoResposta() == null || command.getOrdem() <= 0;
    }

    private boolean requisitoInvalido(SalvarRequisitoVagaDTO command) {
        return command.getVagaId() == null || emBranco(command.getDescricao());
    }

    private boolean emBranco(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private boolean responsaveisInvalidos(java.util.Collection<UUID> responsaveisIds) {
        return responsaveisIds == null || responsaveisIds.isEmpty() || responsaveisIds.contains(null);
    }

    private boolean responsaveisAutorizados(java.util.Collection<UUID> responsaveisIds) {
        for (UUID responsavelId : responsaveisIds) {
            if (!autorizacao.podeManterVagas(responsavelId)) return false;
        }
        return true;
    }

    private OffsetDateTime agora() {
        return OffsetDateTime.now(clock);
    }

    private void preencher(PerguntaVaga pergunta, SalvarPerguntaVagaDTO command) {
        pergunta.setVagaId(command.getVagaId());
        pergunta.setEnunciado(command.getEnunciado());
        pergunta.setTipoResposta(command.getTipoResposta());
        pergunta.setObrigatoria(command.isObrigatoria());
        pergunta.setOrdem(command.getOrdem());
    }

    private void preencher(RequisitoVaga requisito, SalvarRequisitoVagaDTO command) {
        requisito.setVagaId(command.getVagaId());
        requisito.setDescricao(command.getDescricao());
        requisito.setObrigatorio(command.isObrigatorio());
    }

    private TypedResponse<VagaDTO> validarAcesso(UUID usuarioId) {
        return !autorizado(usuarioId) ? vagaErro(403, ACESSO_NAO_AUTORIZADO) : null;
    }

    private TypedResponse<PerguntaVagaDTO> validarAcessoPergunta(UUID usuarioId) {
        return !autorizado(usuarioId) ? perguntaErro(403, ACESSO_NAO_AUTORIZADO) : null;
    }

    private TypedResponse<RequisitoVagaDTO> validarAcessoRequisito(UUID usuarioId) {
        return !autorizado(usuarioId) ? requisitoErro(403, ACESSO_NAO_AUTORIZADO) : null;
    }

    private TypedResponse<Void> validarAcessoVazio(UUID usuarioId) {
        return !autorizado(usuarioId) ? vazioErro(403, ACESSO_NAO_AUTORIZADO) : null;
    }

    private boolean autorizado(UUID usuarioId) {
        return usuarioId != null && autorizacao.podeManterVagas(usuarioId);
    }

    private TypedResponse<VagaDTO> vagaResposta(int status, String mensagem, Vaga vaga) {
        return new TypedResponse<VagaDTO>(status, mensagem, paraDto(vaga));
    }

    private boolean ordenacaoValida(String ordenarPor) {
        return "titulo".equals(ordenarPor) || "status".equals(ordenarPor)
                || "criadoEm".equals(ordenarPor) || "atualizadoEm".equals(ordenarPor);
    }

    private VagaDTO paraDto(Vaga vaga) {
        return new VagaDTO(vaga.getId(), vaga.getResponsaveisIds(), vaga.getTitulo(),
                vaga.getDescricao(), vaga.getStatus());
    }

    private TypedResponse<PerguntaVagaDTO> perguntaResposta(int status, String mensagem, PerguntaVaga pergunta) {
        return new TypedResponse<PerguntaVagaDTO>(status, mensagem, new PerguntaVagaDTO(pergunta.getId(), pergunta.getEnunciado(), pergunta.getTipoResposta(), pergunta.isObrigatoria(), pergunta.getOrdem()));
    }

    private TypedResponse<RequisitoVagaDTO> requisitoResposta(int status, String mensagem, RequisitoVaga requisito) {
        return new TypedResponse<RequisitoVagaDTO>(status, mensagem, new RequisitoVagaDTO(requisito.getId(), requisito.getDescricao(), requisito.isObrigatorio()));
    }

    private TypedResponse<VagaDTO> vagaErro(int status, String mensagem) {
        return new TypedResponse<VagaDTO>(status, mensagem, null);
    }

    private TypedResponse<PerguntaVagaDTO> perguntaErro(int status, String mensagem) {
        return new TypedResponse<PerguntaVagaDTO>(status, mensagem, null);
    }

    private TypedResponse<RequisitoVagaDTO> requisitoErro(int status, String mensagem) {
        return new TypedResponse<RequisitoVagaDTO>(status, mensagem, null);
    }

    private TypedResponse<Void> vazioErro(int status, String mensagem) {
        return new TypedResponse<Void>(status, mensagem, null);
    }
}
