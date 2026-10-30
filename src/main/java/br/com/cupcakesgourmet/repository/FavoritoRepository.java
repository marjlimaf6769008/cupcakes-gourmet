package br.com.cupcakesgourmet.repository;

import br.com.cupcakesgourmet.model.entity.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    @Query("select f from Favorito f join fetch f.cupcake where f.cliente.id = :clienteId")
    List<Favorito> findByClienteId(Long clienteId);
    Optional<Favorito> findByIdAndClienteId(Long id, Long clienteId);
    boolean existsByClienteIdAndCupcakeId(Long clienteId, Long cupcakeId);
}
