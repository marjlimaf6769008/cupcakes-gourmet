package br.com.cupcakesgourmet.model.dto;

import br.com.cupcakesgourmet.model.entity.Cupcake;

import java.math.BigDecimal;

public class ItemCarrinhoDTO {
    private final Cupcake cupcake;
    private int quantidade;

    public ItemCarrinhoDTO(Cupcake cupcake, int quantidade) {
        if (cupcake == null) {
            throw new IllegalArgumentException("O cupcake é obrigatório");
        }
        definirQuantidade(quantidade);
        this.cupcake = cupcake;
    }

    public BigDecimal calcularSubtotal() {
        return cupcake.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }

    public void definirQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva");
        }
        this.quantidade = quantidade;
    }

    public Cupcake getCupcake() {
        return cupcake;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
