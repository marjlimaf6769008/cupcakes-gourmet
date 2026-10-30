package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {
    public enum Tipo { PIX, CARTAO }
    public enum Status { PENDENTE, APROVADO, RECUSADO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Status status = Status.PENDENTE;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    protected Pagamento() {
    }

    public Pagamento(Tipo tipo, Pedido pedido) {
        this.tipo = tipo;
        this.pedido = pedido;
    }

    public Long getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void aprovar() {
        this.status = Status.APROVADO;
        this.dataPagamento = LocalDateTime.now();
    }
    public Pedido getPedido() { return pedido; }
}
