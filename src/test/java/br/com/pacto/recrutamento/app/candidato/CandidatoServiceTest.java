package br.com.pacto.recrutamento.app.ports.out.candidato;

import br.com.pacto.recrutamento.app.dtos.candidato.*;
import br.com.pacto.recrutamento.app.ports.out.candidato.model.CandidaturaDoCandidato;
import br.com.pacto.recrutamento.app.usecases.candidato.CandidatoService;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Candidato;
import br.com.pacto.recrutamento.core.entities.Curriculo;
import br.com.pacto.recrutamento.app.ports.out.curriculo.CurriculoPort;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CandidatoServiceTest {

    private final CandidatoPort repository = new CandidatoRepositoryFalso();
    private final CurriculoRepositoryFalso curriculos = new CurriculoRepositoryFalso();
    private final CandidatoService service = new CandidatoService(repository, curriculos);

    @Test
    void consultaPerfilComResumoDoCurriculoAtual() {
        UUID usuarioId = UUID.randomUUID();
        Candidato candidato = repository.salvar(new Candidato(usuarioId, LocalDate.of(2020, 1, 1)));
        curriculos.atual = new Curriculo(candidato.getId(), "curriculos/cv.pdf", "cv.pdf",
                "application/pdf", 100, "abc");

        TypedResponse<CandidatoDTO> resposta = service.consultarMeuPerfil(
                new ConsultarMeuPerfilDTO(usuarioId));

        assertThat(resposta.getStatusCode()).isEqualTo(200);
        assertThat(resposta.getData().getCurriculo().getNomeOriginal()).isEqualTo("cv.pdf");
    }

    @Test
    void criaPerfilDoUsuarioQuandoAindaNaoExiste() {
        UUID usuarioId = UUID.randomUUID();
        LocalDate dataAdmissao = LocalDate.of(2020, 2, 3);

        TypedResponse<CandidatoDTO> resposta = service.criarCandidato(
                new CriarCandidatoDTO(usuarioId, dataAdmissao));

        assertThat(resposta.getStatusCode()).isEqualTo(201);
        assertThat(resposta.getData().getUsuarioId()).isEqualTo(usuarioId);
        assertThat(resposta.getData().getDataAdmissao()).isEqualTo(dataAdmissao);
    }

    @Test
    void naoCriaSegundoPerfilParaMesmoUsuario() {
        UUID usuarioId = UUID.randomUUID();
        repository.salvar(new Candidato(usuarioId, LocalDate.of(2019, 1, 1)));

        TypedResponse<CandidatoDTO> resposta = service.criarCandidato(
                new CriarCandidatoDTO(usuarioId, LocalDate.of(2020, 2, 3)));

        assertThat(resposta.getStatusCode()).isEqualTo(409);
        assertThat(resposta.getData()).isNull();
    }

    @Test
    void atualizaApenasPerfilAssociadoAoUsuarioInformado() {
        UUID usuarioId = UUID.randomUUID();
        UUID outroUsuarioId = UUID.randomUUID();
        Candidato perfilDoUsuario = repository.salvar(
                new Candidato(usuarioId, LocalDate.of(2019, 1, 1)));
        Candidato perfilDeOutroUsuario = repository.salvar(
                new Candidato(outroUsuarioId, LocalDate.of(2018, 1, 1)));

        TypedResponse<CandidatoDTO> resposta = service.atualizarCandidato(
                new AtualizarCandidatoDTO(usuarioId, LocalDate.of(2022, 4, 5)));

        assertThat(resposta.getStatusCode()).isEqualTo(200);
        assertThat(resposta.getData().getId()).isEqualTo(perfilDoUsuario.getId());
        assertThat(repository.buscarPorUsuarioId(usuarioId).get().getDataAdmissao())
                .isEqualTo(LocalDate.of(2022, 4, 5));
        assertThat(repository.buscarPorUsuarioId(outroUsuarioId).get().getId())
                .isEqualTo(perfilDeOutroUsuario.getId());
        assertThat(repository.buscarPorUsuarioId(outroUsuarioId).get().getDataAdmissao())
                .isEqualTo(LocalDate.of(2018, 1, 1));
    }

    @Test
    void retornaNaoEncontradoAoAtualizarUsuarioSemPerfil() {
        TypedResponse<CandidatoDTO> resposta = service.atualizarCandidato(
                new AtualizarCandidatoDTO(UUID.randomUUID(), LocalDate.now()));

        assertThat(resposta.getStatusCode()).isEqualTo(404);
        assertThat(resposta.getData()).isNull();
    }

    @Test
    void listaSomenteCandidaturasDoProprioUsuarioComEstadoAtualEFeedback() {
        UUID usuarioId = UUID.randomUUID();
        OffsetDateTime criadaEm = OffsetDateTime.parse("2024-04-05T10:15:30-03:00");
        CandidaturaDoCandidato candidatura = new CandidaturaDoCandidato(
                UUID.randomUUID(), UUID.randomUUID(), "Desenvolvedor", StatusCandidatura.EM_ANALISE,
                criadaEm, "Perfil em avaliacao");
        ((CandidatoRepositoryFalso) repository).pagina = new PaginaGenerico<>(
                Collections.singletonList(candidatura), 1);

        TypedPagedResponse<CandidaturaResumoDTO> resposta = service.listarMinhasCandidaturas(
                new ListarMinhasCandidaturasDTO(usuarioId, 0, 10));

        assertThat(((CandidatoRepositoryFalso) repository).ultimoUsuarioConsultado).isEqualTo(usuarioId);
        assertThat(resposta.getStatusCode()).isEqualTo(200);
        assertThat(resposta.getTotalItems()).isEqualTo(1);
        assertThat(resposta.getData()).extracting(CandidaturaResumoDTO::getTituloVaga)
                .containsExactly("Desenvolvedor");
        assertThat(resposta.getData().get(0).getStatus()).isEqualTo(StatusCandidatura.EM_ANALISE);
        assertThat(resposta.getData().get(0).getFeedback()).isEqualTo("Perfil em avaliacao");
    }

    @Test
    void retornaRequisicaoInvalidaComPaginaVaziaQuandoUsuarioNaoFoiInformado() {
        TypedPagedResponse<CandidaturaResumoDTO> resposta = service.listarMinhasCandidaturas(
                new ListarMinhasCandidaturasDTO(null, 0, 10));

        assertThat(resposta.getStatusCode()).isEqualTo(400);
        assertThat(resposta.getData()).isEmpty();
        assertThat(resposta.getPage()).isZero();
        assertThat(resposta.getPageSize()).isEqualTo(10);
    }

    @Test
    void retornaRequisicaoInvalidaComPaginacaoSeguraQuandoPaginaOuTamanhoSaoInvalidos() {
        TypedPagedResponse<CandidaturaResumoDTO> resposta = service.listarMinhasCandidaturas(
                new ListarMinhasCandidaturasDTO(UUID.randomUUID(), -1, 0));

        assertThat(resposta.getStatusCode()).isEqualTo(400);
        assertThat(resposta.getData()).isEmpty();
        assertThat(resposta.getPage()).isZero();
        assertThat(resposta.getPageSize()).isEqualTo(1);
    }

    private static class CandidatoRepositoryFalso implements CandidatoPort {
        private final java.util.Map<UUID, Candidato> perfis = new java.util.HashMap<>();
        private UUID ultimoUsuarioConsultado;
        private PaginaGenerico<CandidaturaDoCandidato> pagina =
                new PaginaGenerico<>(Collections.<CandidaturaDoCandidato>emptyList(), 0);

        @Override
        public boolean existePorUsuarioId(UUID usuarioId) {
            return perfis.containsKey(usuarioId);
        }

        @Override
        public Candidato salvar(Candidato candidato) {
            perfis.put(candidato.getUsuarioId(), candidato);
            return candidato;
        }

        @Override
        public Optional<Candidato> buscarPorUsuarioId(UUID usuarioId) {
            return Optional.ofNullable(perfis.get(usuarioId));
        }

        @Override
        public PaginaGenerico<CandidaturaDoCandidato> listarCandidaturasDoUsuario(
                UUID usuarioId, int page, int pageSize) {
            ultimoUsuarioConsultado = usuarioId;
            return pagina;
        }
    }

    private static class CurriculoRepositoryFalso implements CurriculoPort {
        private Curriculo atual;
        public Optional<Curriculo> buscarAtivoPorCandidato(UUID candidatoId) {
            return Optional.ofNullable(atual).filter(c -> candidatoId.equals(c.getCandidatoId()));
        }
        public Optional<Curriculo> buscarAtivoPorId(UUID id) { return Optional.empty(); }
        public void salvar(Curriculo curriculo) { atual = curriculo; }
        public void substituir(Curriculo anterior, Curriculo novo, OffsetDateTime excluidoEm) {
            atual = novo;
        }
    }
}
