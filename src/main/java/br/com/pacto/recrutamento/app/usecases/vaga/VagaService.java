package br.com.pacto.recrutamento.app.usecases.vaga;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.app.ports.in.vaga.VagaUseCase;
import br.com.pacto.recrutamento.app.ports.out.vaga.AutorizacaoVagaPort;
import br.com.pacto.recrutamento.app.ports.out.vaga.PerguntaVagaPort;
import br.com.pacto.recrutamento.app.ports.out.vaga.RequisitoVagaPort;
import br.com.pacto.recrutamento.app.ports.out.vaga.VagaPort;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

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
        return new TypedResponse<VagaDTO>(status, mensagem, new VagaDTO(vaga.getId(), vaga.getResponsaveisIds(), vaga.getTitulo(), vaga.getDescricao(), vaga.getStatus()));
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
