package br.com.cupcakesgourmet.repository;

import br.com.cupcakesgourmet.model.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository repository;

    @Test
    @DisplayName("deve buscar usuário pelo e-mail")
    void deveBuscarPorEmail() {
        repository.saveAndFlush(new Usuario("Administrador", "admin@example.com",
                "$2a$10$hash", Usuario.Papel.ROLE_ADMIN));

        assertThat(repository.findByEmail("admin@example.com"))
                .isPresent()
                .get()
                .extracting(Usuario::getPapel)
                .isEqualTo(Usuario.Papel.ROLE_ADMIN);
    }
}
