package br.com.cupcakesgourmet.model.dto;

import br.com.cupcakesgourmet.model.entity.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RelatorioPedidoDTO(Long id, Long clienteId, String clienteNome,
                                 BigDecimal valorTotal, LocalDateTime data,
                                 String status) {
    public static RelatorioPedidoDTO from(Pedido pedido) {
        return new RelatorioPedidoDTO(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNomeCompleto(),
                pedido.getValorTotal(),
                pedido.getData(),
                pedido.getStatus().name());
    }
}
