package br.com.pacto.recrutamento.infra.repositorys.templatevaga;

import br.com.pacto.recrutamento.core.entities.Usuario;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.Collection;

public interface AutorizacaoTemplateVagaJpaRepository extends Repository<Usuario, UUID> {
    @Query("select case when count(u) > 0 then true else false end "
            + "from Usuario u join u.papeis p "
            + "where u.id = :id and u.ativo = true and u.excluidoEm is null "
            + "and p.nome = :papel")
    boolean administradorAtivo(@Param("id") UUID id, @Param("papel") NomePapel papel);

    @Query("select case when count(u) > 0 then true else false end "
            + "from Usuario u join u.papeis p "
            + "where u.id = :id and u.ativo = true and u.excluidoEm is null "
            + "and p.nome in :papeis")
    boolean possuiPapelAtivo(@Param("id") UUID id, @Param("papeis") Collection<NomePapel> papeis);

}
