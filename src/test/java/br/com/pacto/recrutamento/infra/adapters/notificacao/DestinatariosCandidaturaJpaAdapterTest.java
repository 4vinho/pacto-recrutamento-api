package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.entities.Usuario;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import br.com.pacto.recrutamento.infra.repositorys.candidatura.CandidaturaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.vaga.VagaJpaRepository;
import br.com.pacto.recrutamento.infra.repositorys.usuario.UsuarioJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DestinatariosCandidaturaJpaAdapterTest {
    @Test
    void buscaResponsaveisDaVagaEUsuarioDaCandidatura() {
        UUID candidaturaId = UUID.randomUUID();
        UUID vagaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID responsavelId = UUID.randomUUID();
        UUID outroResponsavelId = UUID.randomUUID();
        Candidatura candidatura = new Candidatura(usuarioId, vagaId);
        candidatura.setId(candidaturaId);
        Vaga vaga = new Vaga(Arrays.asList(responsavelId, outroResponsavelId), "Vaga", "Descricao");
        vaga.setId(vagaId);
        CandidaturaJpaRepository candidaturas = mock(CandidaturaJpaRepository.class);
        VagaJpaRepository vagas = mock(VagaJpaRepository.class);
        UsuarioJpaRepository usuarios = mock(UsuarioJpaRepository.class);
        Usuario usuario = new Usuario("candidato@pacto.com", "senha");
        usuario.setId(usuarioId);
        usuario.setNome("Maria Silva");
        when(candidaturas.findById(candidaturaId)).thenReturn(Optional.of(candidatura));
        when(vagas.findById(vagaId)).thenReturn(Optional.of(vaga));
        when(usuarios.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarios.findIdsByPapelAtivo(NomePapel.RESPONSAVEL_VAGA))
                .thenReturn(List.of(responsavelId, outroResponsavelId));

        assertThat(new DestinatariosCandidaturaJpaAdapter(candidaturas, vagas, usuarios)
                .buscarPorCandidatura(candidaturaId))
                .hasValueSatisfying(destinatarios -> {
                    assertThat(destinatarios.getResponsaveisIds())
                            .containsExactly(responsavelId, outroResponsavelId);
                    assertThat(destinatarios.getUsuarioId()).isEqualTo(usuarioId);
                    assertThat(destinatarios.getTituloVaga()).isEqualTo("Vaga");
                    assertThat(destinatarios.getNomeCandidato()).isEqualTo("Maria Silva");
                });
    }

    @Test
    void retornaVazioQuandoCandidaturaNaoExiste() {
        UUID candidaturaId = UUID.randomUUID();
        CandidaturaJpaRepository candidaturas = mock(CandidaturaJpaRepository.class);
        VagaJpaRepository vagas = mock(VagaJpaRepository.class);
        UsuarioJpaRepository usuarios = mock(UsuarioJpaRepository.class);
        when(candidaturas.findById(candidaturaId)).thenReturn(Optional.empty());

        assertThat(new DestinatariosCandidaturaJpaAdapter(candidaturas, vagas, usuarios)
                .buscarPorCandidatura(candidaturaId)).isEmpty();
    }
}
