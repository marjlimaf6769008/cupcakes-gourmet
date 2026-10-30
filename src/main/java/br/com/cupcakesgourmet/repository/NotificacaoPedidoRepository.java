package br.com.cupcakesgourmet.repository;

import br.com.cupcakesgourmet.model.entity.NotificacaoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoPedidoRepository extends JpaRepository<NotificacaoPedido, Long> {
    List<NotificacaoPedido> findByLidaFalseOrderByDataCriacaoDesc();
}
