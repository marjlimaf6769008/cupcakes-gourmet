package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes_pedido")
public class NotificacaoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(nullable = false, length = 255)
    private String mensagem;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private boolean lida;

    protected NotificacaoPedido() {
    }

    public NotificacaoPedido(Long pedidoId, String mensagem) {
        this.pedidoId = pedidoId;
        this.mensagem = mensagem;
        this.dataCriacao = LocalDateTime.now();
        this.lida = false;
    }

    public Long getId() { return id; }
    public Long getPedidoId() { return pedidoId; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public boolean isLida() { return lida; }
}
