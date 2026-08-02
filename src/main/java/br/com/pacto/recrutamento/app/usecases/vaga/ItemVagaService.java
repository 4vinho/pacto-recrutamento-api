package br.com.pacto.recrutamento.app.usecases.vaga;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.app.ports.out.vaga.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.enums.StatusVaga;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

final class ItemVagaService {
    private final VagaPort vagas;
    private final PerguntaVagaPort perguntas;
    private final RequisitoVagaPort requisitos;
    private final AutorizacaoVagaPort autorizacao;
    private final Clock clock;

    ItemVagaService(VagaPort vagas, PerguntaVagaPort perguntas, RequisitoVagaPort requisitos,
                    AutorizacaoVagaPort autorizacao, Clock clock) {
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.autorizacao = autorizacao;
        this.clock = clock;
    }

    TypedResponse<PerguntaVagaDTO> criarPergunta(SalvarPerguntaVagaDTO command) {
        if (command == null || perguntaInvalida(command)) return erro(400, DADOS_PERGUNTA_INVALIDOS);
        if (!autorizado(command.getUsuarioSolicitanteId())) return erro(403, ACESSO_NAO_AUTORIZADO);
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return erro(404, VAGA_NAO_ENCONTRADA);
        if (naoEstaEmRascunho(vaga.get())) return erro(422, VAGA_NAO_PODE_SER_EDITADA);
        PerguntaVaga pergunta = new PerguntaVaga();
        preencher(pergunta, command);
        return resposta(201, "Pergunta criada", perguntas.salvar(pergunta));
    }

    TypedResponse<PerguntaVagaDTO> atualizarPergunta(SalvarPerguntaVagaDTO command) {
        if (command == null || command.getPerguntaId() == null || perguntaInvalida(command))
            return erro(400, DADOS_PERGUNTA_INVALIDOS);
        if (!autorizado(command.getUsuarioSolicitanteId())) return erro(403, ACESSO_NAO_AUTORIZADO);
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return erro(404, VAGA_NAO_ENCONTRADA);
        if (naoEstaEmRascunho(vaga.get())) return erro(422, VAGA_NAO_PODE_SER_EDITADA);
        Optional<PerguntaVaga> pergunta = perguntas.buscarAtivaPorId(command.getPerguntaId());
        if (!pergunta.isPresent() || !command.getVagaId().equals(pergunta.get().getVagaId()))
            return erro(404, PERGUNTA_NAO_ENCONTRADA);
        preencher(pergunta.get(), command);
        return resposta(200, "Pergunta atualizada", perguntas.salvar(pergunta.get()));
    }

    TypedResponse<Void> excluirPergunta(ExcluirItemVagaDTO command) {
        if (command == null) return vazio(400, DADOS_PERGUNTA_INVALIDOS);
        if (!autorizado(command.getUsuarioSolicitanteId())) return vazio(403, ACESSO_NAO_AUTORIZADO);
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return vazio(404, VAGA_NAO_ENCONTRADA);
        if (naoEstaEmRascunho(vaga.get())) return vazio(422, VAGA_NAO_PODE_SER_EDITADA);
        Optional<PerguntaVaga> pergunta = perguntas.buscarAtivaPorId(command.getItemId());
        if (!pergunta.isPresent() || !command.getVagaId().equals(pergunta.get().getVagaId()))
            return vazio(404, PERGUNTA_NAO_ENCONTRADA);
        pergunta.get().setExcluidoEm(OffsetDateTime.now(clock));
        perguntas.salvar(pergunta.get());
        return vazio(204, "Pergunta excluida");
    }

    TypedResponse<RequisitoVagaDTO> criarRequisito(SalvarRequisitoVagaDTO command) {
        if (command == null || requisitoInvalido(command)) return erro(400, DADOS_REQUISITO_INVALIDOS);
        if (!autorizado(command.getUsuarioSolicitanteId())) return erro(403, ACESSO_NAO_AUTORIZADO);
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return erro(404, VAGA_NAO_ENCONTRADA);
        if (naoEstaEmRascunho(vaga.get())) return erro(422, VAGA_NAO_PODE_SER_EDITADA);
        RequisitoVaga requisito = new RequisitoVaga();
        preencher(requisito, command);
        return resposta(201, "Requisito criado", requisitos.salvar(requisito));
    }

    TypedResponse<RequisitoVagaDTO> atualizarRequisito(SalvarRequisitoVagaDTO command) {
        if (command == null || command.getRequisitoId() == null || requisitoInvalido(command))
            return erro(400, DADOS_REQUISITO_INVALIDOS);
        if (!autorizado(command.getUsuarioSolicitanteId())) return erro(403, ACESSO_NAO_AUTORIZADO);
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return erro(404, VAGA_NAO_ENCONTRADA);
        if (naoEstaEmRascunho(vaga.get())) return erro(422, VAGA_NAO_PODE_SER_EDITADA);
        Optional<RequisitoVaga> requisito = requisitos.buscarAtivoPorId(command.getRequisitoId());
        if (!requisito.isPresent() || !command.getVagaId().equals(requisito.get().getVagaId()))
            return erro(404, REQUISITO_NAO_ENCONTRADO);
        preencher(requisito.get(), command);
        return resposta(200, "Requisito atualizado", requisitos.salvar(requisito.get()));
    }

    TypedResponse<Void> excluirRequisito(ExcluirItemVagaDTO command) {
        if (command == null) return vazio(400, DADOS_REQUISITO_INVALIDOS);
        if (!autorizado(command.getUsuarioSolicitanteId())) return vazio(403, ACESSO_NAO_AUTORIZADO);
        Optional<Vaga> vaga = vagas.buscarAtivaPorId(command.getVagaId());
        if (!vaga.isPresent()) return vazio(404, VAGA_NAO_ENCONTRADA);
        if (naoEstaEmRascunho(vaga.get())) return vazio(422, VAGA_NAO_PODE_SER_EDITADA);
        Optional<RequisitoVaga> requisito = requisitos.buscarAtivoPorId(command.getItemId());
        if (!requisito.isPresent() || !command.getVagaId().equals(requisito.get().getVagaId()))
            return vazio(404, REQUISITO_NAO_ENCONTRADO);
        requisito.get().setExcluidoEm(OffsetDateTime.now(clock));
        requisitos.salvar(requisito.get());
        return vazio(204, "Requisito excluido");
    }

    private boolean perguntaInvalida(SalvarPerguntaVagaDTO command) {
        return command.getVagaId() == null || emBranco(command.getEnunciado())
                || command.getTipoResposta() == null || command.getOrdem() <= 0;
    }

    private boolean requisitoInvalido(SalvarRequisitoVagaDTO command) {
        return command.getVagaId() == null || emBranco(command.getDescricao());
    }

    private boolean emBranco(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private boolean autorizado(UUID usuarioId) {
        return usuarioId != null && autorizacao.podeManterVagas(usuarioId);
    }

    private boolean naoEstaEmRascunho(Vaga vaga) {
        return vaga.getStatus() != StatusVaga.RASCUNHO;
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

    private TypedResponse<PerguntaVagaDTO> resposta(int status, String mensagem, PerguntaVaga pergunta) {
        return new TypedResponse<>(status, mensagem, new PerguntaVagaDTO(pergunta.getId(),
                pergunta.getEnunciado(), pergunta.getTipoResposta(), pergunta.isObrigatoria(), pergunta.getOrdem()));
    }

    private TypedResponse<RequisitoVagaDTO> resposta(int status, String mensagem, RequisitoVaga requisito) {
        return new TypedResponse<>(status, mensagem, new RequisitoVagaDTO(requisito.getId(),
                requisito.getDescricao(), requisito.isObrigatorio()));
    }

    private <T> TypedResponse<T> erro(int status, String mensagem) {
        return new TypedResponse<>(status, mensagem, null);
    }

    private TypedResponse<Void> vazio(int status, String mensagem) {
        return new TypedResponse<>(status, mensagem, null);
    }
}
