package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Cliente;
import br.com.cupcakesgourmet.model.entity.Usuario;
import br.com.cupcakesgourmet.repository.ClienteRepository;
import br.com.cupcakesgourmet.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private ClienteRepository clienteRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("deve exibir a página de login")
    void deveExibirLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("deve registrar cliente com e-mail normalizado")
    void deveRegistrarCliente() throws Exception {
        when(usuarioRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(clienteRepository.findByEmail("maria@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.existsByCpf("123.456.789-00")).thenReturn(false);
        when(clienteRepository.existsByCpf("123.456.789-00")).thenReturn(false);
        when(passwordEncoder.encode("senha1234")).thenReturn("hash");

        mockMvc.perform(post("/registrar")
                        .param("primeiroNome", " Maria ")
                        .param("sobrenome", " Silva ")
                        .param("cpf", "123.456.789-00")
                        .param("dataNascimento", "01/01/1990")
                        .param("telefone", "(11) 99999-9999")
                        .param("email", "MARIA@EXAMPLE.COM")
                        .param("senha", "senha1234")
                        .param("confirmacaoSenha", "senha1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("sucesso",
                        "Cadastro realizado. Entre para continuar."));

        verify(usuarioRepository).save(any(Usuario.class));
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("deve retornar formulário quando as senhas divergem")
    void deveRejeitarSenhasDivergentes() throws Exception {
        mockMvc.perform(post("/registrar")
                        .param("primeiroNome", "Maria")
                        .param("sobrenome", "Silva")
                        .param("cpf", "123.456.789-00")
                        .param("dataNascimento", "01/01/1990")
                        .param("telefone", "(11) 99999-9999")
                        .param("email", "maria@example.com")
                        .param("senha", "senha1234")
                        .param("confirmacaoSenha", "senha9999"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registrar"))
                .andExpect(model().attributeHasFieldErrors("registro", "confirmacaoSenha"));

        verifyNoInteractions(passwordEncoder);
        verify(usuarioRepository, never()).save(any());
    }
}
