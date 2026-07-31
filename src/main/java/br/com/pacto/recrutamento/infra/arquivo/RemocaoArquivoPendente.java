package br.com.pacto.recrutamento.infra.arquivo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "remocoes_arquivo_pendentes")
public class RemocaoArquivoPendente {
    @Id
    private UUID id;
    @Column(name = "storage_key", nullable = false, unique = true, length = 500)
    private String storageKey;
    @Column(name = "motivo", nullable = false, length = 500)
    private String motivo;
    @Column(name = "tentativas", nullable = false)
    private int tentativas;
    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "ultima_tentativa_em")
    private OffsetDateTime ultimaTentativaEm;

    protected RemocaoArquivoPendente() {
    }

    RemocaoArquivoPendente(UUID id, String storageKey, String motivo,
                           int tentativas, OffsetDateTime criadoEm) {
        this.id = id;
        this.storageKey = storageKey;
        this.motivo = MotivoRemocao.sanitizar(motivo);
        this.tentativas = tentativas;
        this.criadoEm = criadoEm;
    }

    void registrarFalha(String novoMotivo, OffsetDateTime instante) {
        motivo = MotivoRemocao.sanitizar(novoMotivo);
        tentativas++;
        ultimaTentativaEm = instante;
    }

    public UUID getId() { return id; }
    public String getStorageKey() { return storageKey; }
    public String getMotivo() { return motivo; }
    public int getTentativas() { return tentativas; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public OffsetDateTime getUltimaTentativaEm() { return ultimaTentativaEm; }
}
