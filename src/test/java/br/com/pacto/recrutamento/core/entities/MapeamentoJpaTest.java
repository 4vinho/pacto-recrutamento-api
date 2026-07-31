package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusVaga;
import br.com.pacto.recrutamento.core.enums.TipoNotificacao;
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
    @Autowired
    private EntityManager entityManager;

    @Test
    void registraTodasAsEntidadesPersistentesSemDuplicacaoJpa() {
        Set<String> entidades = entityManager.getMetamodel().getEntities().stream()
                .map(tipo -> tipo.getJavaType().getSimpleName())
                .collect(Collectors.toSet());

        assertThat(entidades).containsExactlyInAnyOrder(
                "Usuario", "Papel", "RefreshToken", "TokenRecuperacaoSenha",
                "Candidato", "Curriculo", "Vaga",
                "PerguntaVaga", "RequisitoVaga",
                Candidatura.class.getSimpleName(),
                RespostaCandidatura.class.getSimpleName(), Notificacao.class.getSimpleName(), "TemplateVaga",
                "PerguntaTemplateVaga", "RequisitoTemplateVaga");
    }

    @Test
    @Transactional
    void persisteEntidadesComIdStatusEAuditoria() {
        Vaga vaga = Vaga.restaurar(UUID.randomUUID(), UUID.randomUUID(),
                "Desenvolvedor", "Descricao", StatusVaga.RASCUNHO, null, null, null);
        Notificacao notificacao = notificacaoJpa();

        entityManager.persist(vaga);
        entityManager.persist(notificacao);
        entityManager.flush();

        assertThat(vaga.getId()).isNotNull();
        assertThat(vaga.getCriadoEm()).isNotNull();
        assertThat(notificacao.getId()).isNotNull();
        assertThat(notificacao.getCriadoEm()).isNotNull();
    }

    private Notificacao notificacaoJpa() {
        return new Notificacao(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TipoNotificacao.CANDIDATURA_CRIADA, "Nova candidatura", "Uma candidatura foi criada",
                br.com.pacto.recrutamento.core.enums.StatusNotificacao.PENDENTE, 0, null);
    }
}
