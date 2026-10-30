package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_carrinho")
public class ItemCarrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrinho_id", nullable = false)
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cupcake_id", nullable = false)
    private Cupcake cupcake;

    @Column(nullable = false)
    private int quantidade;

    protected ItemCarrinho() {
    }

    public ItemCarrinho(Carrinho carrinho, Cupcake cupcake, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser positiva");
        this.carrinho = carrinho;
        this.cupcake = cupcake;
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        return cupcake.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }
}
