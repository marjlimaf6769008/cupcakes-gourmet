package br.com.cupcakesgourmet.service;

import br.com.cupcakesgourmet.model.entity.Usuario;
import br.com.cupcakesgourmet.repository.UsuarioRepository;
import br.com.cupcakesgourmet.security.UsuarioPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.GrantedAuthority;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioDetailsService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioDetailsService(usuarioRepository);
    }

    @Test
    @DisplayName("deve normalizar e carregar usuário pelo e-mail")
    void deveCarregarUsuarioPeloEmailNormalizado() {
        Usuario usuario = new Usuario("Maria", "Silva", "123.456.789-00",
                java.time.LocalDate.of(1990, 1, 1), "(11) 99999-9999",
                "maria@example.com", "hash", Usuario.Papel.ROLE_CLIENTE);
        when(usuarioRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(usuario));

        var detalhes = service.loadUserByUsername("  MARIA@EXAMPLE.COM ");

        assertThat(detalhes).isInstanceOf(UsuarioPrincipal.class);
        assertThat(detalhes.getUsername()).isEqualTo("maria@example.com");
        assertThat(detalhes.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_CLIENTE");
        verify(usuarioRepository).findByEmail("maria@example.com");
    }

    @Test
    @DisplayName("deve lançar exceção quando o e-mail não existe")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findByEmail("inexistente@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("inexistente@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado.");
    }
}
