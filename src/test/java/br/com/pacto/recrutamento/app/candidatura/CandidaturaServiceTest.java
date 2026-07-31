package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.app.dtos.candidatura.*;
import br.com.pacto.recrutamento.app.ports.out.candidato.CandidatoPort;
import br.com.pacto.recrutamento.app.ports.out.candidato.model.CandidaturaDoCandidato;
import br.com.pacto.recrutamento.app.usecases.candidatura.CandidaturaService;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.*;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import br.com.pacto.recrutamento.core.enums.StatusVaga;
import br.com.pacto.recrutamento.core.enums.TipoResposta;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CandidaturaServiceTest {
    private final UUID usuarioCandidato = UUID.randomUUID();
    private final UUID candidatoId = UUID.randomUUID();
    private final UUID responsavel = UUID.randomUUID();
    private final Candidatos candidatos = new Candidatos(usuarioCandidato, candidatoId);
    private final Candidaturas candidaturas = new Candidaturas();
    private final Vagas vagas = new Vagas();
    private final Perguntas perguntas = new Perguntas();
    private final Eventos eventos = new Eventos();
    private final CandidaturaService service = new CandidaturaService(candidatos,
            candidaturas, vagas, perguntas, new Autorizacao(), eventos);

    @Test
    void criaCandidaturaEnviadaParaCandidatoEmVagaAbertaEPublicaEventoAposSalvar() {
        Vaga vaga = vagaPublicada();
        vagas.salvar(vaga);

        TypedResponse<CandidaturaDTO> resposta = service.criarCandidatura(
                new CriarCandidaturaDTO(usuarioCandidato, vaga.getId()));

        assertEquals(201, resposta.getStatusCode());
        assertEquals(StatusCandidatura.ENVIADA, resposta.getData().getStatus());
        assertEquals(candidatoId, resposta.getData().getCandidatoId());
        assertEquals(1, eventos.criadas);
        assertTrue(candidaturas.salvouAntesDoEvento);
    }

    @Test
    void criaRascunhoQuandoVagaPossuiPerguntasESoPublicaAposRespostasValidas() {
        Vaga vaga = vagaPublicada();
        vagas.salvar(vaga);
        PerguntaVaga obrigatoria = pergunta(vaga.getId());
        obrigatoria.setObrigatoria(true);
        perguntas.salvar(obrigatoria);

        TypedResponse<CandidaturaDTO> criada = service.criarCandidatura(
                new CriarCandidaturaDTO(usuarioCandidato, vaga.getId()));

        assertEquals(StatusCandidatura.RASCUNHO, criada.getData().getStatus());
        assertEquals(0, eventos.criadas);

        TypedResponse<CandidaturaDTO> enviada = service.registrarRespostas(
                new RegistrarRespostasDTO(usuarioCandidato, criada.getData().getId(),
                        Collections.singletonList(
                                new RespostaCandidaturaDTO(obrigatoria.getId(), "resposta"))));

        assertEquals(200, enviada.getStatusCode());
        assertEquals(StatusCandidatura.ENVIADA, enviada.getData().getStatus());
        assertEquals(1, eventos.criadas);
    }

    @Test
    void rejeitaCriacaoSemPerfilDeCandidatoOuParaVagaQueNaoAceitaCandidaturas() {
        Vaga vaga = vagaPublicada();
        vagas.salvar(vaga);
        assertEquals(403, service.criarCandidatura(new CriarCandidaturaDTO(UUID.randomUUID(), vaga.getId())).getStatusCode());

        Vaga fechada = new Vaga(responsavel, "Java", "Descricao");
        vagas.salvar(fechada);
        assertEquals(422, service.criarCandidatura(new CriarCandidaturaDTO(usuarioCandidato, fechada.getId())).getStatusCode());
    }

    @Test
    void impedeDuplicidadePreviaETraduzViolacaoUnicaConcorrenteEmConflito() {
        Vaga vaga = vagaPublicada();
        vagas.salvar(vaga);
        candidaturas.chavesExistentes.add(chave(candidatoId, vaga.getId()));
        assertEquals(409, service.criarCandidatura(new CriarCandidaturaDTO(usuarioCandidato, vaga.getId())).getStatusCode());

        candidaturas.chavesExistentes.clear();
        candidaturas.falharPorUnicidade = true;
        assertEquals(409, service.criarCandidatura(new CriarCandidaturaDTO(usuarioCandidato, vaga.getId())).getStatusCode());
        assertEquals(0, eventos.criadas);
    }

    @Test
    void falhaPosteriorDoPublicadorNaoDesfazCandidaturaConfirmada() {
        Vaga vaga = vagaPublicada();
        vagas.salvar(vaga);
        eventos.falhar = true;
        TypedResponse<CandidaturaDTO> resposta = service.criarCandidatura(
                new CriarCandidaturaDTO(usuarioCandidato, vaga.getId()));
        assertEquals(201, resposta.getStatusCode());
        assertTrue(candidaturas.dados.containsKey(resposta.getData().getId()));
    }

    @Test
    void registraLoteCompletoSomenteQuandoProprietarioEPerguntasSaoValidas() {
        Candidatura candidatura = candidatura();
        candidaturas.salvar(candidatura);
        PerguntaVaga primeira = pergunta(candidatura.getVagaId());
        PerguntaVaga segunda = pergunta(candidatura.getVagaId());
        perguntas.salvar(primeira);
        perguntas.salvar(segunda);

        TypedResponse<CandidaturaDTO> resposta = service.registrarRespostas(new RegistrarRespostasDTO(
                usuarioCandidato, candidatura.getId(), Arrays.asList(
                new RespostaCandidaturaDTO(primeira.getId(), "sim"),
                new RespostaCandidaturaDTO(segunda.getId(), "nao"))));

        assertEquals(200, resposta.getStatusCode());
        assertEquals(2, candidaturas.respostas.size());
        assertEquals(StatusCandidatura.ENVIADA, candidatura.getStatus());
    }

    @Test
    void exigePerguntasObrigatoriasEValidaTipoDasRespostas() {
        Candidatura candidatura = candidatura();
        candidaturas.salvar(candidatura);
        PerguntaVaga textoObrigatorio = pergunta(candidatura.getVagaId());
        textoObrigatorio.setObrigatoria(true);
        perguntas.salvar(textoObrigatorio);
        PerguntaVaga numero = pergunta(candidatura.getVagaId());
        numero.setTipoResposta(TipoResposta.NUMERO);
        perguntas.salvar(numero);

        assertEquals(422, service.registrarRespostas(new RegistrarRespostasDTO(
                usuarioCandidato, candidatura.getId(), Collections.singletonList(
                new RespostaCandidaturaDTO(numero.getId(), "10")))).getStatusCode());
        assertEquals(422, service.registrarRespostas(new RegistrarRespostasDTO(
                usuarioCandidato, candidatura.getId(), Arrays.asList(
                new RespostaCandidaturaDTO(textoObrigatorio.getId(), "ok"),
                new RespostaCandidaturaDTO(numero.getId(), "dez")))).getStatusCode());
        assertTrue(candidaturas.respostas.isEmpty());
        assertEquals(StatusCandidatura.RASCUNHO, candidatura.getStatus());
    }

    @Test
    void invalidaTodoLoteComVazioDuplicadaOutraVagaOuPerguntaInexistente() {
        Candidatura candidatura = candidatura();
        candidaturas.salvar(candidatura);
        PerguntaVaga pergunta = pergunta(candidatura.getVagaId());
        perguntas.salvar(pergunta);
        PerguntaVaga deOutraVaga = pergunta(UUID.randomUUID());
        perguntas.salvar(deOutraVaga);

        assertEquals(400, service.registrarRespostas(new RegistrarRespostasDTO(usuarioCandidato, candidatura.getId(), Collections.<RespostaCandidaturaDTO>emptyList())).getStatusCode());
        assertEquals(400, service.registrarRespostas(new RegistrarRespostasDTO(usuarioCandidato, candidatura.getId(), Arrays.asList(new RespostaCandidaturaDTO(pergunta.getId(), "a"), new RespostaCandidaturaDTO(pergunta.getId(), "b")))).getStatusCode());
        assertEquals(422, service.registrarRespostas(new RegistrarRespostasDTO(usuarioCandidato, candidatura.getId(), Collections.singletonList(new RespostaCandidaturaDTO(deOutraVaga.getId(), "a")))).getStatusCode());
        assertEquals(422, service.registrarRespostas(new RegistrarRespostasDTO(usuarioCandidato, candidatura.getId(), Collections.singletonList(new RespostaCandidaturaDTO(UUID.randomUUID(), "a")))).getStatusCode());
        assertTrue(candidaturas.respostas.isEmpty());
    }

    @Test
    void impedeRegistroDeRespostasPorNaoProprietario() {
        Candidatura candidatura = candidatura();
        candidaturas.salvar(candidatura);

        TypedResponse<CandidaturaDTO> resposta = service.registrarRespostas(new RegistrarRespostasDTO(
                UUID.randomUUID(), candidatura.getId(), Collections.singletonList(
                new RespostaCandidaturaDTO(UUID.randomUUID(), "resposta"))));

        assertEquals(403, resposta.getStatusCode());
        assertTrue(candidaturas.respostas.isEmpty());
    }

    @Test
    void traduzRespostaJaPersistidaEmConflitoSemPersistenciaParcial() {
        Candidatura candidatura = candidatura();
        candidaturas.salvar(candidatura);
        PerguntaVaga pergunta = pergunta(candidatura.getVagaId());
        perguntas.salvar(pergunta);
        candidaturas.falharRespostasPorUnicidade = true;
        TypedResponse<CandidaturaDTO> resposta = service.registrarRespostas(new RegistrarRespostasDTO(
                usuarioCandidato, candidatura.getId(), Collections.singletonList(
                new RespostaCandidaturaDTO(pergunta.getId(), "valor"))));
        assertEquals(409, resposta.getStatusCode());
        assertTrue(candidaturas.respostas.isEmpty());
    }

    @Test
    void responsavelAutorizadoAlteraStatusEmTransicaoPermitidaEPublicaEventoAposSalvar() {
        Candidatura candidatura = candidatura();
        candidatura.setStatus(StatusCandidatura.ENVIADA);
        candidaturas.salvar(candidatura);
        vagas.salvar(vagaPublicadaComId(candidatura.getVagaId()));

        TypedResponse<CandidaturaDTO> resposta = service.atualizarStatusCandidatura(
                new AtualizarStatusCandidaturaDTO(responsavel, candidatura.getId(), StatusCandidatura.EM_ANALISE));

        assertEquals(200, resposta.getStatusCode());
        assertEquals(StatusCandidatura.EM_ANALISE, candidatura.getStatus());
        assertEquals(1, eventos.statusAlterados);
    }

    @Test
    void bloqueiaResponsavelNaoAutorizadoETransicaoInvalidaSemEvento() {
        Candidatura candidatura = candidatura();
        candidatura.setStatus(StatusCandidatura.ENVIADA);
        candidaturas.salvar(candidatura);
        vagas.salvar(vagaPublicadaComId(candidatura.getVagaId()));
        assertEquals(403, service.atualizarStatusCandidatura(new AtualizarStatusCandidaturaDTO(UUID.randomUUID(), candidatura.getId(), StatusCandidatura.EM_ANALISE)).getStatusCode());
        assertEquals(422, service.atualizarStatusCandidatura(new AtualizarStatusCandidaturaDTO(responsavel, candidatura.getId(), StatusCandidatura.APROVADA)).getStatusCode());
        assertEquals(0, eventos.statusAlterados);
    }

    @Test
    void apenasProprietarioCancelaCandidaturaEmEstadoPermitido() {
        Candidatura candidatura = candidatura();
        candidatura.setStatus(StatusCandidatura.ENVIADA);
        candidaturas.salvar(candidatura);
        assertEquals(403, service.cancelarCandidatura(new CancelarCandidaturaDTO(UUID.randomUUID(), candidatura.getId())).getStatusCode());
        TypedResponse<CandidaturaDTO> cancelada = service.cancelarCandidatura(new CancelarCandidaturaDTO(usuarioCandidato, candidatura.getId()));
        assertEquals(200, cancelada.getStatusCode());
        assertEquals(StatusCandidatura.CANCELADA, candidatura.getStatus());
        assertFalse(candidatura.getCanceladoEm() == null);
        assertEquals(1, eventos.statusAlterados);
        assertEquals(422, service.cancelarCandidatura(new CancelarCandidaturaDTO(usuarioCandidato, candidatura.getId())).getStatusCode());
    }

    @Test
    void consultaSomentePodeSerFeitaPeloProprietarioOuResponsavelAutorizado() {
        Candidatura candidatura = candidatura();
        candidaturas.salvar(candidatura);
        vagas.salvar(vagaPublicadaComId(candidatura.getVagaId()));
        assertEquals(200, service.consultarCandidatura(new ConsultarCandidaturaDTO(usuarioCandidato, candidatura.getId())).getStatusCode());
        assertEquals(200, service.consultarCandidatura(new ConsultarCandidaturaDTO(responsavel, candidatura.getId())).getStatusCode());
        TypedResponse<CandidaturaDTO> negada = service.consultarCandidatura(new ConsultarCandidaturaDTO(UUID.randomUUID(), candidatura.getId()));
        assertEquals(403, negada.getStatusCode());
        assertNull(negada.getData());
    }

    private Candidatura candidatura() {
        return new Candidatura(candidatoId, UUID.randomUUID());
    }

    private Vaga vagaPublicada() {
        return vagaPublicadaComId(UUID.randomUUID());
    }

    private Vaga vagaPublicadaComId(UUID id) {
        Vaga vaga = new Vaga(responsavel, "Java", "Descricao");
        vaga.setId(id);
        vaga.setStatus(StatusVaga.PUBLICADA);
        return vaga;
    }

    private PerguntaVaga pergunta(UUID vagaId) {
        PerguntaVaga pergunta = new PerguntaVaga();
        pergunta.setVagaId(vagaId);
        pergunta.setEnunciado("Pergunta");
        pergunta.setTipoResposta(TipoResposta.TEXTO);
        pergunta.setOrdem(1);
        return pergunta;
    }

    private String chave(UUID candidato, UUID vaga) {
        return candidato + ":" + vaga;
    }

    private static final class Candidatos implements CandidatoPort {
        private final Map<UUID, Candidato> dados = new HashMap<>();

        private Candidatos(UUID usuarioId, UUID candidatoId) {
            Candidato candidato = new Candidato(usuarioId, LocalDate.now());
            candidato.setId(candidatoId);
            dados.put(usuarioId, candidato);
        }

        public boolean existePorUsuarioId(UUID usuarioId) {
            return dados.containsKey(usuarioId);
        }

        public Candidato salvar(Candidato candidato) {
            dados.put(candidato.getUsuarioId(), candidato);
            return candidato;
        }

        public Optional<Candidato> buscarPorUsuarioId(UUID usuarioId) {
            return Optional.ofNullable(dados.get(usuarioId));
        }

        public PaginaGenerico<CandidaturaDoCandidato> listarCandidaturasDoUsuario(
                UUID u, int p, int s) {
            return new PaginaGenerico<>(Collections.<CandidaturaDoCandidato>emptyList(), 0);
        }
    }

    private static final class Vagas implements VagaCandidaturaPort {
        private final Map<UUID, Vaga> dados = new HashMap<>();

        public Optional<Vaga> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }

        void salvar(Vaga v) {
            dados.put(v.getId(), v);
        }
    }

    private static final class Perguntas implements PerguntaCandidaturaPort {
        private final Map<UUID, PerguntaVaga> dados = new HashMap<>();

        public Optional<PerguntaVaga> buscarAtivaPorId(UUID id) {
            return Optional.ofNullable(dados.get(id)).filter(p -> p.getExcluidoEm() == null);
        }

        public List<PerguntaVaga> listarAtivasPorVagaId(UUID vagaId) {
            List<PerguntaVaga> resultado = new ArrayList<>();
            for (PerguntaVaga pergunta : dados.values()) {
                if (vagaId.equals(pergunta.getVagaId()) && pergunta.getExcluidoEm() == null) {
                    resultado.add(pergunta);
                }
            }
            return resultado;
        }

        void salvar(PerguntaVaga p) {
            dados.put(p.getId(), p);
        }
    }

    private final class Candidaturas implements CandidaturaPort {
        private final Map<UUID, Candidatura> dados = new HashMap<>();
        private final Set<String> chavesExistentes = new HashSet<>();
        private final List<RespostaCandidatura> respostas = new ArrayList<>();
        private boolean falharPorUnicidade;
        private boolean falharRespostasPorUnicidade;
        private boolean salvouAntesDoEvento;

        public Optional<Candidatura> buscarPorId(UUID id) {
            return Optional.ofNullable(dados.get(id));
        }

        public boolean existePorCandidatoIdEVagaId(UUID c, UUID v) {
            return chavesExistentes.contains(chave(c, v));
        }

        public Candidatura salvar(Candidatura c) {
            if (falharPorUnicidade) throw new CandidaturaPort.CandidaturaDuplicadaException();
            dados.put(c.getId(), c);
            chavesExistentes.add(chave(c.getCandidatoId(), c.getVagaId()));
            salvouAntesDoEvento = true;
            return c;
        }

        public void finalizarComRespostasAtomicamente(Candidatura candidatura,
                                                       List<RespostaCandidatura> lote) {
            if (falharRespostasPorUnicidade) throw new CandidaturaPort.RespostasDuplicadasException();
            respostas.addAll(lote);
            candidatura.setStatus(StatusCandidatura.ENVIADA);
            dados.put(candidatura.getId(), candidatura);
        }
    }

    private final class Autorizacao implements AutorizacaoResponsavelCandidaturaPort {
        public boolean podeGerenciar(UUID usuarioId, Vaga vaga) {
            return responsavel.equals(usuarioId) && responsavel.equals(vaga.getResponsavelId());
        }
    }

    private final class Eventos implements EventosCandidaturaPort {
        private int criadas;
        private int statusAlterados;
        private boolean falhar;

        public void candidaturaCriada(Candidatura c) {
            assertTrue(candidaturas.salvouAntesDoEvento);
            if (falhar) throw new IllegalStateException();
            criadas++;
        }

        public void statusAlterado(Candidatura c, StatusCandidatura anterior) {
            if (falhar) throw new IllegalStateException();
            statusAlterados++;
        }
    }
}
