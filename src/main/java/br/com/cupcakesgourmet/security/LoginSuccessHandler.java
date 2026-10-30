package br.com.cupcakesgourmet.security;

import br.com.cupcakesgourmet.model.entity.Usuario;
import br.com.cupcakesgourmet.repository.ClienteRepository;
import br.com.cupcakesgourmet.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public LoginSuccessHandler(UsuarioRepository usuarioRepository,
                               ClienteRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ServletException("Usuário autenticado não foi encontrado."));
        clienteRepository.findByEmail(usuario.getEmail())
                .ifPresent(cliente -> request.getSession(true).setAttribute("clienteId", cliente.getId()));

        String destino = usuario.getPapel() == Usuario.Papel.ROLE_ADMIN
                ? "/admin/cupcakes"
                : "/catalogo";
        response.sendRedirect(request.getContextPath() + destino);
    }
}
