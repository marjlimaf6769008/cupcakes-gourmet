package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.dto.PedidoStatusDTO;
import br.com.cupcakesgourmet.model.dto.PerfilDTO;
import br.com.cupcakesgourmet.model.entity.Cliente;
import br.com.cupcakesgourmet.model.entity.Usuario;
import br.com.cupcakesgourmet.model.entity.Cupcake;
import br.com.cupcakesgourmet.model.entity.Favorito;
import br.com.cupcakesgourmet.repository.ClienteRepository;
import br.com.cupcakesgourmet.repository.CupcakeRepository;
import br.com.cupcakesgourmet.repository.FavoritoRepository;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import br.com.cupcakesgourmet.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    private final ClienteRepository clienteRepository;
    private final CupcakeRepository cupcakeRepository;
    private final FavoritoRepository favoritoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteController(ClienteRepository clienteRepository,
                             CupcakeRepository cupcakeRepository,
                             FavoritoRepository favoritoRepository,
                             PedidoRepository pedidoRepository,
                             UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.cupcakeRepository = cupcakeRepository;
        this.favoritoRepository = favoritoRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Cliente cliente = clienteRepository.findById(clienteId(session))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Cliente da sessão não foi encontrado."));
        model.addAttribute("perfil", new PerfilDTO(cliente));
        return "cliente/perfil";
    }

    @PostMapping("/perfil")
    @Transactional
    public String atualizarPerfil(@Valid @ModelAttribute("perfil") PerfilDTO perfil,
                                  BindingResult bindingResult, HttpSession session,
                                  Model model, RedirectAttributes redirectAttributes) {
        Long id = clienteId(session);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Cliente da sessão não foi encontrado."));
        if (!bindingResult.hasFieldErrors("cpf")
                && !cliente.getCpf().equals(perfil.getCpf())
                && (usuarioRepository.existsByCpf(perfil.getCpf()) || clienteRepository.existsByCpf(perfil.getCpf()))) {
            bindingResult.rejectValue("cpf", "cpf.existente", "Este CPF já está cadastrado.");
        }
        if (bindingResult.hasErrors()) {
            perfil.setEmail(cliente.getEmail());
            return "cliente/perfil";
        }
        cliente.atualizarPerfil(perfil.getPrimeiroNome(), perfil.getSobrenome(), perfil.getCpf(),
                perfil.getDataNascimento(), perfil.getTelefone());
        clienteRepository.save(cliente);
        usuarioRepository.findByEmail(cliente.getEmail()).ifPresent(usuario -> {
            usuario.atualizarPerfil(perfil.getPrimeiroNome(), perfil.getSobrenome(), perfil.getCpf(),
                    perfil.getDataNascimento(), perfil.getTelefone());
            usuarioRepository.save(usuario);
        });
        redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso.");
        return "redirect:/cliente/perfil";
    }

    @GetMapping("/pedidos/status")
    @ResponseBody
    public List<PedidoStatusDTO> statusPedidos(HttpSession session) {
        Long clienteId = clienteId(session);
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(PedidoStatusDTO::from)
                .toList();
    }

    @GetMapping("/favoritos")
    public String favoritos(HttpSession session, Model model) {
        model.addAttribute("favoritos", favoritoRepository.findByClienteId(clienteId(session)));
        return "cliente/favoritos";
    }

    @PostMapping("/favoritos/adicionar/{cupcakeId}")
    public String adicionarFavorito(@PathVariable Long cupcakeId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Long clienteId = clienteId(session);
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Cliente da sessão não foi encontrado."));
        Cupcake cupcake = cupcakeRepository.findById(cupcakeId).orElse(null);

        if (cupcake == null) {
            redirectAttributes.addFlashAttribute("erro", "Cupcake não encontrado.");
        } else if (!favoritoRepository.existsByClienteIdAndCupcakeId(clienteId, cupcakeId)) {
            favoritoRepository.save(new Favorito(cliente, cupcake));
            redirectAttributes.addFlashAttribute("sucesso", "Cupcake adicionado aos favoritos.");
        } else {
            redirectAttributes.addFlashAttribute("sucesso", "Esse cupcake já está nos favoritos.");
        }
        return "redirect:/cliente/favoritos";
    }

    @PostMapping("/favoritos/remover/{id}")
    public String removerFavorito(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        favoritoRepository.findByIdAndClienteId(id, clienteId(session)).ifPresentOrElse(
                favorito -> {
                    favoritoRepository.delete(favorito);
                    redirectAttributes.addFlashAttribute("sucesso", "Favorito removido.");
                },
                () -> redirectAttributes.addFlashAttribute("erro", "Favorito não encontrado."));
        return "redirect:/cliente/favoritos";
    }

    private Long clienteId(HttpSession session) {
        Object value = session.getAttribute("clienteId");
        if (!(value instanceof Number number)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "É necessário estar autenticado como cliente.");
        }
        return number.longValue();
    }
}
