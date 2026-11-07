package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import br.com.cupcakesgourmet.model.session.Carrinho;
import br.com.cupcakesgourmet.repository.CupcakeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarrinhoController.class)
@AutoConfigureMockMvc(addFilters = false)
class CarrinhoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CupcakeRepository cupcakeRepository;

    @MockitoBean
    private Carrinho carrinho;

    @Test
    @DisplayName("deve exibir o carrinho")
    void deveExibirCarrinho() throws Exception {
        mockMvc.perform(get("/carrinho"))
                .andExpect(status().isOk())
                .andExpect(view().name("carrinho"))
                .andExpect(model().attribute("carrinho", carrinho));
    }

    @Test
    @DisplayName("deve adicionar cupcake válido e redirecionar para o carrinho")
    void deveAdicionarCupcakeValido() throws Exception {
        Cupcake cupcake = new Cupcake("Red Velvet", "Cacau",
                new BigDecimal("12.50"), null);
        when(cupcakeRepository.findById(1L)).thenReturn(Optional.of(cupcake));

        mockMvc.perform(post("/carrinho/adicionar")
                        .param("cupcakeId", "1")
                        .param("quantidade", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"))
                .andExpect(flash().attribute("sucesso", "Cupcake adicionado ao carrinho."));

        verify(carrinho).adicionarCupcake(cupcake, 2);
    }

    @Test
    @DisplayName("deve rejeitar quantidade inválida sem consultar o repositório")
    void deveRejeitarQuantidadeInvalida() throws Exception {
        mockMvc.perform(post("/carrinho/adicionar")
                        .param("cupcakeId", "1")
                        .param("quantidade", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalogo"))
                .andExpect(flash().attribute("erro", "A quantidade deve ser positiva."));

        verifyNoInteractions(cupcakeRepository, carrinho);
    }

    @Test
    @DisplayName("deve informar quando o cupcake não existe")
    void deveInformarCupcakeInexistente() throws Exception {
        when(cupcakeRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/carrinho/adicionar")
                        .param("cupcakeId", "99")
                        .param("quantidade", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalogo"))
                .andExpect(flash().attribute("erro", "Cupcake não encontrado."));

        verify(carrinho, never()).adicionarCupcake(any(), anyInt());
    }

    @Test
    @DisplayName("deve remover e atualizar itens do carrinho")
    void deveRemoverEAtualizarItens() throws Exception {
        mockMvc.perform(post("/carrinho/remover/3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"));
        mockMvc.perform(post("/carrinho/atualizar")
                        .param("cupcakeId", "3")
                        .param("quantidade", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"));

        verify(carrinho).removerItem(3L);
        verify(carrinho).atualizarQuantidade(3L, 4);
    }
}
