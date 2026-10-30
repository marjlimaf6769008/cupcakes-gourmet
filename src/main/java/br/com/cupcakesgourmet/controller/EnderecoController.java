package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Cliente;
import br.com.cupcakesgourmet.model.entity.Endereco;
import br.com.cupcakesgourmet.repository.ClienteRepository;
import br.com.cupcakesgourmet.repository.EnderecoRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente/enderecos")
public class EnderecoController {
    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;

    public EnderecoController(EnderecoRepository enderecoRepository,
                              ClienteRepository clienteRepository) {
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        Long clienteId = clienteId(session);
        model.addAttribute("enderecos", enderecoRepository.findByClienteId(clienteId));
        return "cliente/enderecos/index";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) String redirect,
                       Model model) {
        model.addAttribute("endereco", new Endereco(null));
        model.addAttribute("redirect", redirect);
        return "cliente/enderecos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("endereco") Endereco endereco,
                         BindingResult bindingResult,
                         @RequestParam(required = false) String redirect,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Long clienteId = clienteId(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("redirect", redirect);
            return "cliente/enderecos/form";
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Cliente da sessão não foi encontrado."));
        endereco.setCliente(cliente);
        endereco.setEnderecoPrincipal(endereco.isEnderecoPrincipal()
                || enderecoRepository.findByClienteId(clienteId).isEmpty());
        enderecoRepository.save(endereco);

        redirectAttributes.addFlashAttribute("sucesso", "Endereço salvo com sucesso.");
        return "checkout".equals(redirect) ? "redirect:/checkout" : "redirect:/cliente/enderecos";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, HttpSession session,
                          RedirectAttributes redirectAttributes) {
        enderecoRepository.findByIdAndClienteId(id, clienteId(session)).ifPresentOrElse(
                enderecoRepository::delete,
                () -> redirectAttributes.addFlashAttribute("erro", "Endereço não encontrado."));
        return "redirect:/cliente/enderecos";
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
