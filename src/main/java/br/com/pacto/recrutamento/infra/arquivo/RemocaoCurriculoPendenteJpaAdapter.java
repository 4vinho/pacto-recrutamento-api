package br.com.pacto.recrutamento.infra.arquivo;

import br.com.pacto.recrutamento.app.ports.curriculo.RemocaoCurriculoPendente;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class RemocaoCurriculoPendenteJpaAdapter implements RemocaoCurriculoPendente {
    private final EntityManager entityManager;

    public RemocaoCurriculoPendenteJpaAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void registrar(String storageKey, String motivo) {
        entityManager.createNativeQuery(
                        "INSERT INTO remocoes_arquivo_pendentes "
                                + "(id, storage_key, motivo, tentativas, criado_em) "
                                + "VALUES (:id, :storageKey, :motivo, 0, :criadoEm) "
                                + "ON CONFLICT (storage_key) DO UPDATE SET motivo = EXCLUDED.motivo")
                .setParameter("id", UUID.randomUUID())
                .setParameter("storageKey", storageKey)
                .setParameter("motivo", MotivoRemocao.sanitizar(motivo))
                .setParameter("criadoEm", OffsetDateTime.now())
                .executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<RemocaoArquivoPendente> buscarPendentes(int limite) {
        return entityManager.createQuery(
                        "select r from RemocaoArquivoPendente r order by r.criadoEm",
                        RemocaoArquivoPendente.class)
                .setMaxResults(limite)
                .getResultList();
    }

    @Transactional
    public void concluir(UUID id) {
        RemocaoArquivoPendente pendencia =
                entityManager.find(RemocaoArquivoPendente.class, id);
        if (pendencia != null) entityManager.remove(pendencia);
    }

    @Transactional
    public void registrarFalha(UUID id, String motivo, OffsetDateTime instante) {
        RemocaoArquivoPendente pendencia =
                entityManager.find(RemocaoArquivoPendente.class, id);
        if (pendencia != null) pendencia.registrarFalha(motivo, instante);
    }
}
