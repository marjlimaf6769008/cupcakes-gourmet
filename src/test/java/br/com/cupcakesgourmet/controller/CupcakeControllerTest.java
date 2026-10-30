package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import br.com.cupcakesgourmet.repository.CupcakeRepository;
import br.com.cupcakesgourmet.repository.FavoritoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CupcakeController.class, AdminCupcakeController.class})
@AutoConfigureMockMvc(addFilters = false)
class CupcakeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CupcakeRepository cupcakeRepository;

    @MockitoBean
    private FavoritoRepository favoritoRepository;

    @Test
    @DisplayName("GET /catalogo deve retornar catálogo com status 200")
    void deveExibirCatalogo() throws Exception {
        when(cupcakeRepository.findAll()).thenReturn(List.of(
                new Cupcake("Red Velvet", "Cacau", new BigDecimal("12.50"), null)));

        mockMvc.perform(get("/catalogo"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalogo"))
                .andExpect(model().attributeExists("cupcakes"));
    }

    @Test
    @DisplayName("POST /admin/cupcakes/salvar deve redirecionar após cadastro válido")
    void deveCadastrarCupcakeERedirecionar() throws Exception {
        when(cupcakeRepository.save(any(Cupcake.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/admin/cupcakes/salvar")
                .param("nome", "Red Velvet")
                        .param("descricao", "Massa de cacau")
                        .param("preco", "12.50")
                        .param("fotoUrl", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/cupcakes"));

        verify(cupcakeRepository).save(any(Cupcake.class));
    }
}
