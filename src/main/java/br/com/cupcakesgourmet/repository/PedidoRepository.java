package br.com.cupcakesgourmet.repository;

import br.com.cupcakesgourmet.model.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Query("select p from Pedido p join fetch p.cliente order by p.data desc")
    List<Pedido> findAllComCliente();

    List<Pedido> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> findByStatus(Pedido.Status status);
    List<Pedido> findByClienteId(Long clienteId);

    @Query("select p from Pedido p join fetch p.cliente " +
            "where p.data >= :inicio and p.data < :fim " +
            "and p.status <> br.com.cupcakesgourmet.model.entity.Pedido.Status.CANCELADO " +
            "order by p.data desc")
    List<Pedido> findVendasByDataBetween(@Param("inicio") LocalDateTime inicio,
                                         @Param("fim") LocalDateTime fim);

    @Query("select count(p), coalesce(sum(p.valorTotal), 0) from Pedido p " +
            "where p.data >= :inicio and p.data < :fim " +
            "and p.status <> br.com.cupcakesgourmet.model.entity.Pedido.Status.CANCELADO")
    List<Object[]> resumirVendasByDataBetween(@Param("inicio") LocalDateTime inicio,
                                         @Param("fim") LocalDateTime fim);

    @Query("select distinct p from Pedido p " +
            "left join fetch p.cliente " +
            "left join fetch p.itens i left join fetch i.cupcake " +
            "where p.id = :id")
    Optional<Pedido> findDetalhadoById(@Param("id") Long id);

    @Query("select distinct p from Pedido p " +
            "left join fetch p.cliente " +
            "left join fetch p.itens i left join fetch i.cupcake " +
            "where p.id = :id and p.cliente.id = :clienteId")
    Optional<Pedido> findDetalhadoByIdAndClienteId(@Param("id") Long id,
                                                    @Param("clienteId") Long clienteId);
}
