package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusVaga;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;
import br.com.pacto.recrutamento.infra.candidatura.CandidaturaJpaEntity;
import br.com.pacto.recrutamento.infra.candidatura.RespostaCandidaturaJpaEntity;
import br.com.pacto.recrutamento.infra.notificacao.NotificacaoJpaEntity;
import br.com.pacto.recrutamento.infra.templatevaga.PerguntaTemplateVagaJpaEntity;
import br.com.pacto.recrutamento.infra.templatevaga.RequisitoTemplateVagaJpaEntity;
import br.com.pacto.recrutamento.infra.templatevaga.TemplateVagaJpaEntity;
import br.com.pacto.recrutamento.infra.vaga.VagaJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MapeamentoJpaTest {
    @Autowired private EntityManager entityManager;

    @Test
    void registraTodasAsEntidadesPersistentesSemDuplicacaoJpa() {
        Set<String> entidades = entityManager.getMetamodel().getEntities().stream()
                .map(tipo -> tipo.getJavaType().getSimpleName())
                .collect(Collectors.toSet());

        assertThat(entidades).containsExactlyInAnyOrder(
                "Usuario", "Papel", "RefreshToken", "TokenRecuperacaoSenha",
                "Candidato", "Curriculo", "VagaJpaEntity",
                "PerguntaVagaJpaEntity", "RequisitoVagaJpaEntity",
                CandidaturaJpaEntity.class.getSimpleName(),
                RespostaCandidaturaJpaEntity.class.getSimpleName(), "NotificacaoJpaEntity", "TemplateVagaJpaEntity",
                "PerguntaTemplateVagaJpaEntity", "RequisitoTemplateVagaJpaEntity");
    }

    @Test
    @Transactional
    void persisteEntidadesComIdStatusEAuditoria() {
        VagaJpaEntity vaga = new VagaJpaEntity(UUID.randomUUID(), UUID.randomUUID(),
                "Desenvolvedor", "Descricao", StatusVaga.RASCUNHO, null, null, null);
        NotificacaoJpaEntity notificacao = notificacaoJpa();

        entityManager.persist(vaga);
        entityManager.persist(notificacao);
        entityManager.flush();

        assertThat(vaga.getId()).isNotNull();
        assertThat(vaga.getCriadoEm()).isNotNull();
        assertThat(notificacao.getId()).isNotNull();
        assertThat(notificacao.getCriadoEm()).isNotNull();
    }

    private NotificacaoJpaEntity notificacaoJpa() {
        NotificacaoJpaEntity notificacao = new NotificacaoJpaEntity();
        notificacao.setId(UUID.randomUUID());
        notificacao.setEventoId(UUID.randomUUID());
        notificacao.setUsuarioId(UUID.randomUUID());
        notificacao.setTipo(TipoNotificacao.CANDIDATURA_CRIADA);
        notificacao.setTitulo("Nova candidatura");
        notificacao.setMensagem("Uma candidatura foi criada");
        notificacao.setStatus(br.com.pacto.recrutamento.core.enums.StatusNotificacao.PENDENTE);
        notificacao.setCriadoEm(java.time.OffsetDateTime.now());
        return notificacao;
    }
}
