package br.com.cupcakesgourmet.model.dto;

import br.com.cupcakesgourmet.model.entity.NotificacaoPedido;

import java.time.LocalDateTime;

public record NotificacaoPedidoDTO(Long id, Long pedidoId, String mensagem,
                                   LocalDateTime dataCriacao, boolean lida) {
    public static NotificacaoPedidoDTO from(NotificacaoPedido notificacao) {
        return new NotificacaoPedidoDTO(notificacao.getId(), notificacao.getPedidoId(),
                notificacao.getMensagem(), notificacao.getDataCriacao(), notificacao.isLida());
    }
}
