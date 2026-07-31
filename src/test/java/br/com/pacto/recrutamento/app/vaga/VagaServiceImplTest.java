package br.com.pacto.recrutamento.app.ports.vaga;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.app.serviceImpl.VagaServiceImpl;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.enums.StatusVaga;
import br.com.pacto.recrutamento.core.enums.TipoResposta;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VagaServiceImplTest {
    private final UUID administrador = UUID.randomUUID();
    private final UUID naoAutorizado = UUID.randomUUID();
    private final MemoriaVagas vagas = new MemoriaVagas();
    private final MemoriaPerguntas perguntas = new MemoriaPerguntas();
    private final MemoriaRequisitos requisitos = new MemoriaRequisitos();
    private final VagaServiceImpl service = new VagaServiceImpl(vagas, perguntas, requisitos,
            usuarioId -> administrador.equals(usuarioId), Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void criaVagaParaResponsavelAutorizado() {
        TypedResponse<VagaDTO> resposta = service.criarVaga(new CriarVagaDTO(administrador, "Dev Java", "Descricao"));

        assertEquals(201, resposta.getStatusCode());
        assertEquals(administrador, resposta.getData().getResponsavelId());
        assertEquals(StatusVaga.RASCUNHO, resposta.getData().getStatus());
    }

    @Test
    void rejeitaCriacaoPorUsuarioSemPapelAutorizado() {
        TypedResponse<VagaDTO> resposta = service.criarVaga(new CriarVagaDTO(naoAutorizado, "Dev Java", "Descricao"));

        assertEquals(403, resposta.getStatusCode());
        assertNull(resposta.getData());
    }

    @Test
    void atualizaApenasVagaAtivaEAutorizada() {
        Vaga vaga = vaga();
        vagas.salvar(vaga);

        TypedResponse<VagaDTO> resposta = service.atualizarVaga(new AtualizarVagaDTO(administrador, vaga.getId(), "Novo", "Nova descricao"));

        assertEquals(200, resposta.getStatusCode());
        assertEquals("Novo", vaga.getTitulo());
    }

    @Test
    void rejeitaTransicaoInvalidaDeStatus() {
        Vaga vaga = vaga();
        vagas.salvar(vaga);

        TypedResponse<VagaDTO> resposta = service.alterarStatusVaga(new AlterarStatusVagaDTO(administrador, vaga.getId(), StatusVaga.ENCERRADA));

        assertEquals(422, resposta.getStatusCode());
        assertEquals(StatusVaga.RASCUNHO, vaga.getStatus());
    }

    @Test
    void excluiVagaLogicamenteEPassaAImpedirAlteracoes() {
        Vaga vaga = vaga();
        vagas.salvar(vaga);

        assertEquals(204, service.excluirVaga(new ExcluirVagaDTO(administrador, vaga.getId())).getStatusCode());
        assertNotNull(vaga.getExcluidoEm());
        assertEquals(404, service.atualizarVaga(new AtualizarVagaDTO(administrador, vaga.getId(), "Novo", "Descricao")).getStatusCode());
    }

    @Test
    void naoAlteraPerguntaDeOutraVaga() {
        Vaga vaga = vaga();
        Vaga outraVaga = vaga();
        vagas.salvar(vaga);
        vagas.salvar(outraVaga);
        PerguntaVaga pergunta = pergunta(outraVaga.getId());
        perguntas.salvar(pergunta);

        TypedResponse<PerguntaVagaDTO> resposta = service.atualizarPerguntaDaVaga(new SalvarPerguntaVagaDTO(
                administrador, vaga.getId(), pergunta.getId(), "Alterada", TipoResposta.TEXTO, true, 1));

        assertEquals(404, resposta.getStatusCode());
        assertEquals("Pergunta", pergunta.getEnunciado());
    }

    @Test
    void criaPerguntaComOrdemPositivaEExcluiLogicamente() {
        Vaga vaga = vaga();
        vagas.salvar(vaga);

        TypedResponse<PerguntaVagaDTO> invalida = service.criarPerguntaDaVaga(new SalvarPerguntaVagaDTO(administrador, vaga.getId(), null, "Pergunta", TipoResposta.TEXTO, true, 0));
        TypedResponse<PerguntaVagaDTO> criada = service.criarPerguntaDaVaga(new SalvarPerguntaVagaDTO(administrador, vaga.getId(), null, "Pergunta", TipoResposta.TEXTO, true, 1));

        assertEquals(400, invalida.getStatusCode());
        assertEquals(201, criada.getStatusCode());
        UUID perguntaId = criada.getData().getId();
        assertEquals(204, service.excluirPerguntaDaVaga(new ExcluirItemVagaDTO(administrador, vaga.getId(), perguntaId)).getStatusCode());
        assertNotNull(perguntas.dados.get(perguntaId).getExcluidoEm());
    }

    @Test
    void naoAlteraRequisitoDeOutraVagaEExcluiLogicamente() {
        Vaga vaga = vaga();
        Vaga outraVaga = vaga();
        vagas.salvar(vaga);
        vagas.salvar(outraVaga);
        RequisitoVaga requisito = requisito(outraVaga.getId());
        requisitos.salvar(requisito);

        assertEquals(404, service.atualizarRequisitoDaVaga(new SalvarRequisitoVagaDTO(administrador, vaga.getId(), requisito.getId(), "Novo", true)).getStatusCode());
        TypedResponse<RequisitoVagaDTO> criada = service.criarRequisitoDaVaga(new SalvarRequisitoVagaDTO(administrador, vaga.getId(), null, "Java", true));
        UUID requisitoId = criada.getData().getId();
        assertEquals(204, service.excluirRequisitoDaVaga(new ExcluirItemVagaDTO(administrador, vaga.getId(), requisitoId)).getStatusCode());
        assertNotNull(requisitos.dados.get(requisitoId).getExcluidoEm());
    }

    @Test
    void naoExcluiFilhosAtravesDeOutraVaga() {
        Vaga vaga = vaga();
        Vaga outraVaga = vaga();
        vagas.salvar(vaga);
        vagas.salvar(outraVaga);
        PerguntaVaga pergunta = pergunta(outraVaga.getId());
        RequisitoVaga requisito = requisito(outraVaga.getId());
        perguntas.salvar(pergunta);
        requisitos.salvar(requisito);

        assertEquals(404, service.excluirPerguntaDaVaga(
                new ExcluirItemVagaDTO(administrador, vaga.getId(), pergunta.getId())).getStatusCode());
        assertEquals(404, service.excluirRequisitoDaVaga(
                new ExcluirItemVagaDTO(administrador, vaga.getId(), requisito.getId())).getStatusCode());
        assertNull(pergunta.getExcluidoEm());
        assertNull(requisito.getExcluidoEm());
    }

    @Test
    void usuarioSemPapelNaoConsegueManterFilhos() {
        Vaga vaga = vaga();
        vagas.salvar(vaga);

        TypedResponse<PerguntaVagaDTO> resposta = service.criarPerguntaDaVaga(
                new SalvarPerguntaVagaDTO(naoAutorizado, vaga.getId(), null,
                        "Pergunta", TipoResposta.TEXTO, true, 1));

        assertEquals(403, resposta.getStatusCode());
        assertEquals(0, perguntas.dados.size());
    }

    private Vaga vaga() {
        return new Vaga(administrador, "Titulo", "Descricao");
    }

    private PerguntaVaga pergunta(UUID vagaId) {
        PerguntaVaga pergunta = new PerguntaVaga();
        pergunta.setVagaId(vagaId);
        pergunta.setEnunciado("Pergunta");
        pergunta.setTipoResposta(TipoResposta.TEXTO);
        pergunta.setOrdem(1);
        return pergunta;
    }

    private RequisitoVaga requisito(UUID vagaId) {
        RequisitoVaga requisito = new RequisitoVaga();
        requisito.setVagaId(vagaId);
        requisito.setDescricao("Java");
        return requisito;
    }

    private static final class MemoriaVagas implements VagaAdapter {
        private final Map<UUID, Vaga> dados = new HashMap<>();

        public Optional<Vaga> buscarAtivaPorId(UUID id) {
            return Optional.ofNullable(dados.get(id)).filter(v -> v.getExcluidoEm() == null);
        }

        public Vaga salvar(Vaga vaga) {
            dados.put(vaga.getId(), vaga);
            return vaga;
        }
    }

    private static final class MemoriaPerguntas implements PerguntaVagaAdapter {
        private final Map<UUID, PerguntaVaga> dados = new HashMap<>();

        public Optional<PerguntaVaga> buscarAtivaPorId(UUID id) {
            return Optional.ofNullable(dados.get(id)).filter(p -> p.getExcluidoEm() == null);
        }

        public PerguntaVaga salvar(PerguntaVaga pergunta) {
            if (pergunta.getId() == null) pergunta.setId(UUID.randomUUID());
            dados.put(pergunta.getId(), pergunta);
            return pergunta;
        }
    }

    private static final class MemoriaRequisitos implements RequisitoVagaAdapter {
        private final Map<UUID, RequisitoVaga> dados = new HashMap<>();

        public Optional<RequisitoVaga> buscarAtivoPorId(UUID id) {
            return Optional.ofNullable(dados.get(id)).filter(r -> r.getExcluidoEm() == null);
        }

        public RequisitoVaga salvar(RequisitoVaga requisito) {
            if (requisito.getId() == null) requisito.setId(UUID.randomUUID());
            dados.put(requisito.getId(), requisito);
            return requisito;
        }
    }
}
