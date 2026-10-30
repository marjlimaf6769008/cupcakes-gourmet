package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Pedido;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/admin/pedidos")
public class AdminPedidoController {
    private final PedidoRepository pedidoRepository;

    public AdminPedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAllComCliente());
        return "admin/pedidos/index";
    }

    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoRepository.findDetalhadoById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido não encontrado."));
        model.addAttribute("pedido", pedido);
        model.addAttribute("statusValues", Arrays.asList(Pedido.Status.values()));
        return "admin/pedidos/detalhes";
    }

    @PostMapping("/atualizar-status/{id}")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam Pedido.Status status,
                                  RedirectAttributes redirectAttributes) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido não encontrado."));
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
        redirectAttributes.addFlashAttribute("sucesso",
                "Status do pedido #" + pedido.getId() + " atualizado com sucesso.");
        return "redirect:/admin/pedidos";
    }
}
