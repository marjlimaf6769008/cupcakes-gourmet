package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Cliente;
import br.com.cupcakesgourmet.model.entity.Pedido;
import br.com.cupcakesgourmet.model.session.Carrinho;
import br.com.cupcakesgourmet.repository.EnderecoRepository;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import br.com.cupcakesgourmet.service.CheckoutService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Carrinho carrinho;

    @MockitoBean
    private CheckoutService checkoutService;

    @MockitoBean
    private PedidoRepository pedidoRepository;

    @MockitoBean
    private EnderecoRepository enderecoRepository;

    @Test
    @DisplayName("deve redirecionar carrinho vazio para a página do carrinho")
    void deveRedirecionarCarrinhoVazio() throws Exception {
        when(carrinho.getItens()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"))
                .andExpect(flash().attribute("erro",
                        "Adicione itens ao carrinho antes de finalizar."));

        verifyNoInteractions(enderecoRepository);
    }

    @Test
    @DisplayName("deve processar checkout e redirecionar para o sucesso")
    void deveProcessarCheckout() throws Exception {
        when(checkoutService.finalizarCompra(eq(carrinho), eq("PIX"), eq("chave"),
                eq(7L), eq(8L))).thenReturn(42L);

        mockMvc.perform(post("/checkout/processar")
                        .sessionAttr("clienteId", 7L)
                        .param("formaPagamento", "PIX")
                        .param("dadosPagamento", "chave")
                        .param("enderecoId", "8"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/sucesso/42"));
    }

    @Test
    @DisplayName("deve devolver erro do checkout como mensagem flash")
    void deveTratarErroDoCheckout() throws Exception {
        when(checkoutService.finalizarCompra(any(), anyString(), any(), eq(7L), eq(8L)))
                .thenThrow(new IllegalArgumentException("Selecione um endereço de entrega."));

        mockMvc.perform(post("/checkout/processar")
                        .sessionAttr("clienteId", 7L)
                        .param("formaPagamento", "PIX")
                        .param("dadosPagamento", "chave")
                        .param("enderecoId", "8"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout"))
                .andExpect(flash().attribute("erro", "Selecione um endereço de entrega."));
    }

    @Test
    @DisplayName("deve exibir apenas o pedido pertencente ao cliente autenticado")
    void deveExibirSucessoDoPedido() throws Exception {
        Cliente cliente = new Cliente("Maria", "Silva", "123.456.789-00",
                LocalDate.of(1990, 1, 1), "(11) 99999-9999",
                "maria@example.com", "hash");
        Pedido pedido = new Pedido(cliente);
        when(pedidoRepository.findDetalhadoById(42L)).thenReturn(Optional.of(pedido));
        setId(cliente, 7L);

        mockMvc.perform(get("/checkout/sucesso/42").sessionAttr("clienteId", 7L))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/sucesso"))
                .andExpect(model().attribute("pedido", pedido));
    }

    private void setId(Cliente cliente, Long id) throws Exception {
        var field = Cliente.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(cliente, id);
    }
}
