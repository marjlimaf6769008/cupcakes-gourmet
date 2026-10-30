package br.com.cupcakesgourmet.model.dto;

import br.com.cupcakesgourmet.model.entity.Pedido;

public record PedidoStatusDTO(Long id, String status) {
    public static PedidoStatusDTO from(Pedido pedido) {
        return new PedidoStatusDTO(pedido.getId(), pedido.getStatus().name());
    }
}
