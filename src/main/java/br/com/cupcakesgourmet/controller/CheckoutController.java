package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Pedido;
import br.com.cupcakesgourmet.repository.EnderecoRepository;
import br.com.cupcakesgourmet.model.session.Carrinho;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import br.com.cupcakesgourmet.service.CheckoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    private final Carrinho carrinho;
    private final CheckoutService checkoutService;
    private final PedidoRepository pedidoRepository;
    private final EnderecoRepository enderecoRepository;

    public CheckoutController(Carrinho carrinho, CheckoutService checkoutService,
                              PedidoRepository pedidoRepository,
                               EnderecoRepository enderecoRepository) {
        this.carrinho = carrinho;
        this.checkoutService = checkoutService;
        this.pedidoRepository = pedidoRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @GetMapping
    public String checkout(Model model, RedirectAttributes redirectAttributes,
                           HttpSession session) {
        if (carrinho.getItens().isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Adicione itens ao carrinho antes de finalizar.");
            return "redirect:/carrinho";
        }
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("enderecos", enderecoRepository.findByClienteId(clienteId(session)));
        return "checkout/index";
    }

    @PostMapping("/processar")
    public String processar(@RequestParam String formaPagamento,
                            @RequestParam(required = false) String dadosPagamento,
                            @RequestParam(required = false) Long enderecoId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            Long pedidoId = checkoutService.finalizarCompra(
                    carrinho, formaPagamento, dadosPagamento, clienteId(session), enderecoId);
            return "redirect:/checkout/sucesso/" + pedidoId;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/sucesso/{id}")
    public String sucesso(@PathVariable Long id, HttpSession session, Model model) {
        Long clienteId = clienteId(session);
        Pedido pedido = pedidoRepository.findDetalhadoById(id)
                .filter(item -> item.getCliente().getId().equals(clienteId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido não encontrado."));
        model.addAttribute("pedido", pedido);
        return "checkout/sucesso";
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
