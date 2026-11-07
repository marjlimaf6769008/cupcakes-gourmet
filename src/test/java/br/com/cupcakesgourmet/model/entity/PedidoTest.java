package br.com.cupcakesgourmet.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PedidoTest {

    @Test
    @DisplayName("deve criar pedido aguardando pagamento e recalcular o total dos itens")
    void deveCriarPedidoERecalcularTotal() {
        Cliente cliente = cliente();
        Pedido pedido = new Pedido(cliente);
        Cupcake cupcake = new Cupcake("Red Velvet", "Massa de cacau",
                new BigDecimal("12.50"), null);

        pedido.adicionarItem(cupcake, 2);

        assertThat(pedido.getCliente()).isSameAs(cliente);
        assertThat(pedido.getData()).isNotNull();
        assertThat(pedido.getStatus()).isEqualTo(Pedido.Status.AGUARDANDO_PAGAMENTO);
        assertThat(pedido.getItens()).hasSize(1);
        assertThat(pedido.getItens().get(0).getQuantidade()).isEqualTo(2);
        assertThat(pedido.getValorTotal()).isEqualByComparingTo("25.00");
    }

    @Test
    @DisplayName("deve copiar endereço e dados de contato para o pedido")
    void deveCopiarDadosDeEntregaEContato() {
        Cliente cliente = cliente();
        Endereco endereco = new Endereco(cliente);
        endereco.setLogradouro("Rua das Flores");
        endereco.setNumero("100");
        endereco.setComplemento("Apto 2");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");
        endereco.setCep("01000-000");
        endereco.setNomeDestinatario("Maria Silva");
        Pedido pedido = new Pedido(cliente);

        pedido.definirEnderecoEntrega(endereco);
        pedido.definirDadosContato(cliente);

        assertThat(pedido.getLogradouroEntrega()).isEqualTo("Rua das Flores");
        assertThat(pedido.getNumeroEntrega()).isEqualTo("100");
        assertThat(pedido.getComplementoEntrega()).isEqualTo("Apto 2");
        assertThat(pedido.getCidadeEntrega()).isEqualTo("São Paulo");
        assertThat(pedido.getUfEntrega()).isEqualTo("SP");
        assertThat(pedido.getCepEntrega()).isEqualTo("01000-000");
        assertThat(pedido.getNomeDestinatario()).isEqualTo("Maria Silva");
        assertThat(pedido.getCpfCliente()).isEqualTo(cliente.getCpf());
        assertThat(pedido.getTelefoneCliente()).isEqualTo(cliente.getTelefone());
    }

    private Cliente cliente() {
        return new Cliente("Maria", "Silva", "123.456.789-00",
                LocalDate.of(1990, 1, 1), "(11) 99999-9999",
                "maria@example.com", "hash");
    }
}
