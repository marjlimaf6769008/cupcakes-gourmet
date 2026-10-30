package br.com.cupcakesgourmet.model.session;

import br.com.cupcakesgourmet.model.dto.ItemCarrinhoDTO;
import br.com.cupcakesgourmet.model.entity.Cupcake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CarrinhoTest {
    private Carrinho carrinho;
    private Cupcake redVelvet;
    private Cupcake limao;

    @BeforeEach
    void setUp() throws Exception {
        carrinho = new Carrinho();
        redVelvet = cupcake(1L, "Red Velvet", "12.50");
        limao = cupcake(2L, "Limao", "10.00");
    }

    @Test
    @DisplayName("deve adicionar itens e calcular subtotais e total geral")
    void deveAdicionarItensECalcularTotais() {
        carrinho.adicionarCupcake(redVelvet, 2);
        carrinho.adicionarCupcake(limao, 1);

        assertThat(carrinho.getItens()).hasSize(2);
        assertThat(carrinho.getItens())
                .extracting(ItemCarrinhoDTO::calcularSubtotal)
                .containsExactly(new BigDecimal("25.00"), new BigDecimal("10.00"));
        assertThat(carrinho.calcularTotalGeral()).isEqualByComparingTo("35.00");
    }

    @Test
    @DisplayName("deve atualizar a quantidade de um item existente")
    void deveAtualizarQuantidade() {
        carrinho.adicionarCupcake(redVelvet, 1);

        carrinho.atualizarQuantidade(redVelvet.getId(), 4);

        assertThat(carrinho.getItens().get(0).getQuantidade()).isEqualTo(4);
        assertThat(carrinho.calcularTotalGeral()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("deve remover item e recalcular o total")
    void deveRemoverItem() {
        carrinho.adicionarCupcake(redVelvet, 2);
        carrinho.adicionarCupcake(limao, 1);

        carrinho.removerItem(redVelvet.getId());

        assertThat(carrinho.getItens()).hasSize(1);
        assertThat(carrinho.getItens().get(0).getCupcake()).isSameAs(limao);
        assertThat(carrinho.calcularTotalGeral()).isEqualByComparingTo("10.00");
    }

    @Test
    @DisplayName("deve limpar todos os itens do carrinho")
    void deveLimparCarrinho() {
        carrinho.adicionarCupcake(redVelvet, 2);
        carrinho.adicionarCupcake(limao, 1);

        carrinho.limparCarrinho();

        assertThat(carrinho.getItens()).isEmpty();
        assertThat(carrinho.calcularTotalGeral()).isEqualByComparingTo("0.00");
    }

    private Cupcake cupcake(Long id, String nome, String preco) throws Exception {
        Cupcake cupcake = new Cupcake(nome, "Descrição", new BigDecimal(preco), null);
        Field field = Cupcake.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(cupcake, id);
        return cupcake;
    }
}
