package br.com.pacto.recrutamento.app.usecases.vaga;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.app.ports.in.vaga.VagaUseCase;
import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final CandidaturaPort candidaturas;
    private final Clock clock;
    private final ItemVagaService itens;

    public VagaService(VagaPort vagas, PerguntaVagaPort perguntas,
                       RequisitoVagaPort requisitos, AutorizacaoVagaPort autorizacao,
                       CandidaturaPort candidaturas, Clock clock) {
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.autorizacao = autorizacao;
        this.candidaturas = candidaturas;
        this.clock = clock;
        this.itens = new ItemVagaService(vagas, perguntas, requisitos, autorizacao, clock);
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
        UUID excluirCandidaturasDoUsuarioId = autorizado(query.getUsuarioId())
                ? null : query.getUsuarioId();
        PaginaGenerico<Vaga> pagina = vagas.listar(query.getBusca(), status, query.getPage(),
                query.getPageSize(), query.getOrdenarPor(), query.isAscendente(),
                excluirCandidaturasDoUsuarioId);
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
    @Transactional
    public TypedResponse<VagaDetalheDTO> criarVagaCompleta(CriarVagaCompletaDTO command) {
        if (comandoCompletoInvalido(command))
            return new TypedResponse<>(400, DADOS_VAGA_INVALIDOS, null);
        if (!autorizacao.podeManterVagas(command.getUsuarioSolicitanteId()))
            return new TypedResponse<>(403, ACESSO_NAO_AUTORIZADO, null);
        if (!responsaveisAutorizados(command.getResponsaveisIds()))
            return new TypedResponse<>(422, RESPONSAVEL_VAGA_INVALIDO, null);

        Vaga vaga = vagas.salvar(new Vaga(command.getResponsaveisIds(), command.getTitulo(),
                command.getDescricao()));
        List<PerguntaVagaDTO> perguntasDto = command.getPerguntas().stream().map(item -> {
            PerguntaVaga pergunta = new PerguntaVaga();
            pergunta.setVagaId(vaga.getId());
            pergunta.setEnunciado(item.getEnunciado());
            pergunta.setTipoResposta(item.getTipoResposta());
            pergunta.setObrigatoria(item.isObrigatoria());
            pergunta.setOrdem(item.getOrdem());
            PerguntaVaga salva = perguntas.salvar(pergunta);
            return new PerguntaVagaDTO(salva.getId(), salva.getEnunciado(), salva.getTipoResposta(),
                    salva.isObrigatoria(), salva.getOrdem());
        }).collect(Collectors.toList());
        List<RequisitoVagaDTO> requisitosDto = command.getRequisitos().stream().map(item -> {
            RequisitoVaga requisito = new RequisitoVaga();
            requisito.setVagaId(vaga.getId());
            requisito.setDescricao(item.getDescricao());
            requisito.setObrigatorio(item.isObrigatorio());
            RequisitoVaga salvo = requisitos.salvar(requisito);
            return new RequisitoVagaDTO(salvo.getId(), salvo.getDescricao(), salvo.isObrigatorio());
        }).collect(Collectors.toList());

        VagaDetalheDTO detalhe = new VagaDetalheDTO(vaga.getId(), vaga.getResponsaveisIds(),
                vaga.getTitulo(), vaga.getDescricao(), vaga.getStatus(), requisitosDto, perguntasDto);
        return new TypedResponse<>(201, "Vaga completa criada", detalhe);
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
        if (naoEstaEmRascunho(vaga.get())) return vagaErro(422, VAGA_NAO_PODE_SER_EDITADA);
        vaga.get().setTitulo(command.getTitulo());
        vaga.get().setDescricao(command.getDescricao());
        vaga.get().setResponsaveisIds(command.getResponsaveisIds());
        return vagaResposta(200, "Vaga atualizada", vagas.salvar(vaga.get()));
    }

    @Override
    @Transactional
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
        Vaga salva = vagas.salvar(vaga.get());
        if (salva.getStatus() == StatusVaga.CANCELADA) cancelarCandidaturas(salva.getId());
        return vagaResposta(200, "Status da vaga atualizado", salva);
    }

    @Override
    @Transactional
    public TypedResponse<Void> excluirVaga(ExcluirVagaDTO command) {
        if (command == null) return vazioErro(400, DADOS_VAGA_INVALIDOS);
        TypedResponse<Void> acesso = validarAcessoVazio(command.getUsuarioSolicitanteId());
        if (acesso != null) return acesso;
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return vazioErro(404, VAGA_NAO_ENCONTRADA);
        vaga.get().setExcluidoEm(agora());
        vagas.salvar(vaga.get());
        cancelarCandidaturas(vaga.get().getId());
        return new TypedResponse<Void>(204, "Vaga excluida", null);
    }

    @Override
    public TypedResponse<PerguntaVagaDTO> criarPerguntaDaVaga(SalvarPerguntaVagaDTO command) {
        return itens.criarPergunta(command);
    }

    @Override
    public TypedResponse<PerguntaVagaDTO> atualizarPerguntaDaVaga(SalvarPerguntaVagaDTO command) {
        return itens.atualizarPergunta(command);
    }

    @Override
    public TypedResponse<Void> excluirPerguntaDaVaga(ExcluirItemVagaDTO command) {
        return itens.excluirPergunta(command);
    }

    @Override
    public TypedResponse<RequisitoVagaDTO> criarRequisitoDaVaga(SalvarRequisitoVagaDTO command) {
        return itens.criarRequisito(command);
    }

    @Override
    public TypedResponse<RequisitoVagaDTO> atualizarRequisitoDaVaga(SalvarRequisitoVagaDTO command) {
        return itens.atualizarRequisito(command);
    }

    @Override
    public TypedResponse<Void> excluirRequisitoDaVaga(ExcluirItemVagaDTO command) {
        return itens.excluirRequisito(command);
    }

    private boolean camposVagaInvalidos(String titulo, String descricao) {
        return emBranco(titulo) || emBranco(descricao);
    }

    private boolean naoEstaEmRascunho(Vaga vaga) {
        return vaga.getStatus() != StatusVaga.RASCUNHO;
    }

    private boolean comandoCompletoInvalido(CriarVagaCompletaDTO command) {
        if (command == null || responsaveisInvalidos(command.getResponsaveisIds())
                || camposVagaInvalidos(command.getTitulo(), command.getDescricao())
                || command.getPerguntas() == null || command.getRequisitos() == null)
            return true;
        return command.getPerguntas().stream().anyMatch(item -> item == null
                || emBranco(item.getEnunciado()) || item.getTipoResposta() == null || item.getOrdem() <= 0)
                || command.getRequisitos().stream().anyMatch(item -> item == null
                || emBranco(item.getDescricao()));
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

    private void cancelarCandidaturas(UUID vagaId) {
        OffsetDateTime data = agora();
        candidaturas.listarCancelaveisPorVaga(vagaId).forEach(candidatura -> {
            candidatura.cancelar(data);
            candidaturas.salvar(candidatura);
        });
    }

    private TypedResponse<VagaDTO> validarAcesso(UUID usuarioId) {
        return !autorizado(usuarioId) ? vagaErro(403, ACESSO_NAO_AUTORIZADO) : null;
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

    private TypedResponse<VagaDTO> vagaErro(int status, String mensagem) {
        return new TypedResponse<VagaDTO>(status, mensagem, null);
    }

    private TypedResponse<Void> vazioErro(int status, String mensagem) {
        return new TypedResponse<Void>(status, mensagem, null);
    }
}
