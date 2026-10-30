package br.com.cupcakesgourmet.service;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarrinhoServiceTest {
    private CarrinhoService carrinho;

    @BeforeEach
    void setUp() {
        carrinho = new CarrinhoService();
    }

    @Test
    @DisplayName("deve calcular o total somando preço multiplicado pela quantidade")
    void deveCalcularTotalSomandoPrecoMultiplicadoPelaQuantidade() {
        Cupcake redVelvet = new Cupcake("Red Velvet", "Massa de cacau", new BigDecimal("12.50"), null);
        Cupcake limao = new Cupcake("Limao", "Creme de limao", new BigDecimal("10.00"), null);

        carrinho.adicionar(redVelvet, 2);
        carrinho.adicionar(limao, 1);

        assertThat(carrinho.calcularTotal()).isEqualByComparingTo("35.00");
    }

    @Test
    @DisplayName("deve acumular quantidades do mesmo cupcake")
    void deveAcumularQuantidadeDoMesmoCupcake() {
        Cupcake cupcake = new Cupcake("Red Velvet", "Massa de cacau", new BigDecimal("12.50"), null);

        carrinho.adicionar(cupcake, 1);
        carrinho.adicionar(cupcake, 2);

        assertThat(carrinho.getItens()).containsEntry(cupcake, 3);
        assertThat(carrinho.calcularTotal()).isEqualByComparingTo("37.50");
    }

    @Test
    @DisplayName("deve rejeitar quantidade não positiva")
    void deveRejeitarQuantidadeNaoPositiva() {
        Cupcake cupcake = new Cupcake("Red Velvet", "Massa de cacau", new BigDecimal("12.50"), null);

        assertThatThrownBy(() -> carrinho.adicionar(cupcake, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A quantidade deve ser positiva");
        assertThat(carrinho.getItens()).isEmpty();
    }

    @Test
    @DisplayName("deve remover cupcake e atualizar o total")
    void deveRemoverCupcake() {
        Cupcake cupcake = new Cupcake("Red Velvet", "Massa de cacau", new BigDecimal("12.50"), null);
        carrinho.adicionar(cupcake, 2);

        carrinho.remover(cupcake);

        assertThat(carrinho.getItens()).isEmpty();
        assertThat(carrinho.calcularTotal()).isEqualByComparingTo("0.00");
    }
}
