package br.com.pacto.recrutamento.app.ports.out.vaga;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.app.usecases.vaga.VagaService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VagaServiceTest {
    private final UUID administrador = UUID.randomUUID();
    private final UUID naoAutorizado = UUID.randomUUID();
    private final UUID segundoResponsavel = UUID.randomUUID();
    private final MemoriaVagas vagas = new MemoriaVagas();
    private final MemoriaPerguntas perguntas = new MemoriaPerguntas();
    private final MemoriaRequisitos requisitos = new MemoriaRequisitos();
    private final VagaService service = new VagaService(vagas, perguntas, requisitos,
            usuarioId -> administrador.equals(usuarioId) || segundoResponsavel.equals(usuarioId),
            Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void criaVagaParaResponsavelAutorizado() {
        TypedResponse<VagaDTO> resposta = service.criarVaga(new CriarVagaDTO(administrador,
                Arrays.asList(administrador, segundoResponsavel), "Dev Java", "Descricao"));

        assertEquals(201, resposta.getStatusCode());
        assertEquals(2, resposta.getData().getResponsaveisIds().size());
        assertTrue(resposta.getData().getResponsaveisIds().contains(administrador));
        assertTrue(resposta.getData().getResponsaveisIds().contains(segundoResponsavel));
        assertEquals(StatusVaga.RASCUNHO, resposta.getData().getStatus());
    }

    @Test
    void criaVagaCompletaComPerguntasERequisitos() {
        CriarVagaCompletaDTO command = new CriarVagaCompletaDTO(administrador,
                Collections.singleton(administrador), "Dev Java", "Descricao",
                Collections.singletonList(new CriarVagaCompletaDTO.Pergunta(
                        "Fale sobre Java", TipoResposta.TEXTO, true, 1)),
                Collections.singletonList(new CriarVagaCompletaDTO.Requisito("Java", true)));

        TypedResponse<VagaDetalheDTO> resposta = service.criarVagaCompleta(command);

        assertEquals(201, resposta.getStatusCode());
        assertEquals(1, vagas.dados.size());
        assertEquals(1, perguntas.dados.size());
        assertEquals(1, requisitos.dados.size());
        assertEquals(resposta.getData().getId(), perguntas.dados.values().iterator().next().getVagaId());
        assertEquals(resposta.getData().getId(), requisitos.dados.values().iterator().next().getVagaId());
        assertEquals(1, resposta.getData().getPerguntas().size());
        assertEquals(1, resposta.getData().getRequisitos().size());
    }

    @Test
    void validaTodoLoteAntesDeCriarVagaCompleta() {
        CriarVagaCompletaDTO command = new CriarVagaCompletaDTO(administrador,
                Collections.singleton(administrador), "Dev Java", "Descricao",
                Collections.singletonList(new CriarVagaCompletaDTO.Pergunta(
                        "Pergunta", TipoResposta.TEXTO, true, 0)),
                Collections.<CriarVagaCompletaDTO.Requisito>emptyList());

        TypedResponse<VagaDetalheDTO> resposta = service.criarVagaCompleta(command);

        assertEquals(400, resposta.getStatusCode());
        assertTrue(vagas.dados.isEmpty());
        assertTrue(perguntas.dados.isEmpty());
        assertTrue(requisitos.dados.isEmpty());
    }

    @Test
    void listaSomentePublicadasParaUsuarioComum() {
        Vaga publicada = vaga();
        publicada.setStatus(StatusVaga.PUBLICADA);
        vagas.salvar(publicada);
        vagas.salvar(vaga());

        TypedPagedResponse<VagaDTO> resposta = service.listarVagas(new ListarVagasDTO(
                naoAutorizado, null, null, 0, 20, "criadoEm", false));

        assertEquals(200, resposta.getStatusCode());
        assertEquals(1, resposta.getTotalItems());
        assertEquals(StatusVaga.PUBLICADA, resposta.getData().get(0).getStatus());
    }

    @Test
    void administradorPodeFiltrarVagasPorStatus() {
        vagas.salvar(vaga());

        TypedPagedResponse<VagaDTO> resposta = service.listarVagas(new ListarVagasDTO(
                administrador, "Titulo", StatusVaga.RASCUNHO, 0, 20, "titulo", true));

        assertEquals(1, resposta.getTotalItems());
    }

    @Test
    void consultaVagaComPerguntasERequisitos() {
        Vaga vaga = vaga();
        vaga.setStatus(StatusVaga.PUBLICADA);
        vagas.salvar(vaga);
        perguntas.salvar(pergunta(vaga.getId()));
        requisitos.salvar(requisito(vaga.getId()));

        TypedResponse<VagaDetalheDTO> resposta = service.consultarVaga(
                new ConsultarVagaDTO(naoAutorizado, vaga.getId()));

        assertEquals(200, resposta.getStatusCode());
        assertEquals(1, resposta.getData().getPerguntas().size());
        assertEquals(1, resposta.getData().getRequisitos().size());
    }

    @Test
    void ocultaRascunhoDeUsuarioComum() {
        Vaga vaga = vaga();
        vagas.salvar(vaga);

        assertEquals(404, service.consultarVaga(
                new ConsultarVagaDTO(naoAutorizado, vaga.getId())).getStatusCode());
    }

    @Test
    void rejeitaCriacaoPorUsuarioSemPapelAutorizado() {
        TypedResponse<VagaDTO> resposta = service.criarVaga(new CriarVagaDTO(naoAutorizado,
                Collections.singleton(administrador), "Dev Java", "Descricao"));

        assertEquals(403, resposta.getStatusCode());
        assertNull(resposta.getData());
    }

    @Test
    void atualizaApenasVagaAtivaEAutorizada() {
        Vaga vaga = vaga();
        vagas.salvar(vaga);

        TypedResponse<VagaDTO> resposta = service.atualizarVaga(new AtualizarVagaDTO(administrador,
                vaga.getId(), Collections.singleton(segundoResponsavel), "Novo", "Nova descricao"));

        assertEquals(200, resposta.getStatusCode());
        assertEquals("Novo", vaga.getTitulo());
        assertTrue(vaga.possuiResponsavel(segundoResponsavel));
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
        assertEquals(404, service.atualizarVaga(new AtualizarVagaDTO(administrador, vaga.getId(),
                Collections.singleton(administrador), "Novo", "Descricao")).getStatusCode());
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

    private static final class MemoriaVagas implements VagaPort {
        private final Map<UUID, Vaga> dados = new HashMap<>();

        public PaginaGenerico<Vaga> listar(String busca, StatusVaga status, int page, int pageSize,
                                           String ordenarPor, boolean ascendente,
                                           UUID excluirCandidaturasDoUsuarioId) {
            List<Vaga> itens = dados.values().stream()
                    .filter(v -> v.getExcluidoEm() == null)
                    .filter(v -> status == null || v.getStatus() == status)
                    .filter(v -> busca == null || v.getTitulo().contains(busca)
                            || v.getDescricao().contains(busca))
                    .collect(Collectors.toList());
            return new PaginaGenerico<>(itens, itens.size());
        }

        public Optional<Vaga> buscarAtivaPorId(UUID id) {
            return Optional.ofNullable(dados.get(id)).filter(v -> v.getExcluidoEm() == null);
        }

        public Vaga salvar(Vaga vaga) {
            dados.put(vaga.getId(), vaga);
            return vaga;
        }
    }

    private static final class MemoriaPerguntas implements PerguntaVagaPort {
        private final Map<UUID, PerguntaVaga> dados = new HashMap<>();

        public List<PerguntaVaga> listarAtivasPorVagaId(UUID vagaId) {
            return dados.values().stream().filter(p -> vagaId.equals(p.getVagaId()))
                    .filter(p -> p.getExcluidoEm() == null).collect(Collectors.toList());
        }

        public Optional<PerguntaVaga> buscarAtivaPorId(UUID id) {
            return Optional.ofNullable(dados.get(id)).filter(p -> p.getExcluidoEm() == null);
        }

        public PerguntaVaga salvar(PerguntaVaga pergunta) {
            if (pergunta.getId() == null) pergunta.setId(UUID.randomUUID());
            dados.put(pergunta.getId(), pergunta);
            return pergunta;
        }
    }

    private static final class MemoriaRequisitos implements RequisitoVagaPort {
        private final Map<UUID, RequisitoVaga> dados = new HashMap<>();

        public List<RequisitoVaga> listarAtivosPorVagaId(UUID vagaId) {
            return dados.values().stream().filter(r -> vagaId.equals(r.getVagaId()))
                    .filter(r -> r.getExcluidoEm() == null).collect(Collectors.toList());
        }

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
