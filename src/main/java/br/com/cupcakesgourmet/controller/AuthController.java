package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.dto.RegistroDTO;
import br.com.cupcakesgourmet.model.entity.Cliente;
import br.com.cupcakesgourmet.model.entity.Usuario;
import br.com.cupcakesgourmet.repository.ClienteRepository;
import br.com.cupcakesgourmet.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository,
                           ClienteRepository clienteRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registrar")
    public String registrar(Model model) {
        model.addAttribute("registro", new RegistroDTO());
        return "auth/registrar";
    }

    @PostMapping("/registrar")
    @Transactional
    public String registrar(@Valid @ModelAttribute("registro") RegistroDTO registro,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        String email = registro.getEmail() == null
                ? ""
                : registro.getEmail().trim().toLowerCase(Locale.ROOT);

        if (!bindingResult.hasFieldErrors("email")
                && (usuarioRepository.existsByEmail(email) || clienteRepository.findByEmail(email).isPresent())) {
            bindingResult.rejectValue("email", "email.existente", "Este e-mail já está cadastrado.");
        }
        if (!bindingResult.hasFieldErrors("cpf")
                && (usuarioRepository.existsByCpf(registro.getCpf())
                || clienteRepository.existsByCpf(registro.getCpf()))) {
            bindingResult.rejectValue("cpf", "cpf.existente", "Este CPF já está cadastrado.");
        }
        if (!bindingResult.hasFieldErrors("senha")
                && !registro.getSenha().equals(registro.getConfirmacaoSenha())) {
            bindingResult.rejectValue("confirmacaoSenha", "senha.divergente", "As senhas não conferem.");
        }
        if (bindingResult.hasErrors()) {
            return "auth/registrar";
        }

        String senhaHash = passwordEncoder.encode(registro.getSenha());
        usuarioRepository.save(new Usuario(registro.getPrimeiroNome().trim(), registro.getSobrenome().trim(),
                registro.getCpf(), registro.getDataNascimento(), registro.getTelefone(), email, senhaHash,
                Usuario.Papel.ROLE_CLIENTE));
        clienteRepository.save(new Cliente(registro.getPrimeiroNome().trim(), registro.getSobrenome().trim(),
                registro.getCpf(), registro.getDataNascimento(), registro.getTelefone(), email, senhaHash));

        redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado. Entre para continuar.");
        return "redirect:/login";
    }
}
