package br.com.cupcakesgourmet.config;

import br.com.cupcakesgourmet.model.entity.Usuario;
import br.com.cupcakesgourmet.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AdminDataInitializer implements CommandLineRunner {
    private static final String ADMIN_EMAIL = "admin@cupcakes.com";

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminDataInitializer(UsuarioRepository usuarioRepository,
                                BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        Usuario administrador = new Usuario(
                "Administrador",
                "Sistema",
                "111.111.111-11",
                LocalDate.of(1980, 1, 1),
                "(11) 99999-9999",
                ADMIN_EMAIL,
                bCryptPasswordEncoder.encode("admin123"),
                Usuario.Papel.ROLE_ADMIN
        );
        usuarioRepository.save(administrador);
    }
}
