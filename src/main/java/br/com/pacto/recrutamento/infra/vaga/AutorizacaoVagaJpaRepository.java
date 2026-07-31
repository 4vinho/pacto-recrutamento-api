package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.entities.Usuario;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

interface AutorizacaoVagaJpaRepository extends Repository<Usuario, UUID> {
    @Query("select case when count(u) > 0 then true else false end " +
            "from Usuario u join u.papeis p " +
            "where u.id = :usuarioId and u.ativo = true and u.excluidoEm is null " +
            "and p.nome in :papeis")
    boolean possuiPapelAtivo(@Param("usuarioId") UUID usuarioId,
                             @Param("papeis") Collection<NomePapel> papeis);
}
