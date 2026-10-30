package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Pagamento;
import br.com.cupcakesgourmet.model.entity.Pedido;
import br.com.cupcakesgourmet.repository.PagamentoRepository;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cliente/pedidos")
public class ClientePedidoController {
    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;

    public ClientePedidoController(PedidoRepository pedidoRepository,
                                   PagamentoRepository pagamentoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    @GetMapping
    public String index(HttpSession session, Model model) {
        model.addAttribute("pedidos", pedidoRepository.findByClienteId(clienteId(session)));
        return "cliente/pedidos/index";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, HttpSession session, Model model) {
        Long clienteId = clienteId(session);
        Pedido pedido = pedidoRepository.findDetalhadoByIdAndClienteId(id, clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido não encontrado."));

        model.addAttribute("pedido", pedido);
        model.addAttribute("pagamento", pagamentoRepository.findByPedidoId(id).orElse(null));
        model.addAttribute("taxaEntrega", BigDecimal.ZERO);
        return "cliente/pedidos/detalhes";
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
