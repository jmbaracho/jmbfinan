package br.com.jmb.finan.repository;

import br.com.jmb.finan.domain.Lancamento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Lancamento entity.
 */
@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    default Optional<Lancamento> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Lancamento> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Lancamento> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct lancamento from Lancamento lancamento left join fetch lancamento.conta left join fetch lancamento.categoria",
        countQuery = "select count(distinct lancamento) from Lancamento lancamento"
    )
    Page<Lancamento> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct lancamento from Lancamento lancamento left join fetch lancamento.conta left join fetch lancamento.categoria")
    List<Lancamento> findAllWithToOneRelationships();

    @Query(
        "select lancamento from Lancamento lancamento left join fetch lancamento.conta left join fetch lancamento.categoria where lancamento.id =:id"
    )
    Optional<Lancamento> findOneWithToOneRelationships(@Param("id") Long id);
}
