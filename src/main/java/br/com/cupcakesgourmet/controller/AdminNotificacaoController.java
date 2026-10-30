package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.dto.NotificacaoPedidoDTO;
import br.com.cupcakesgourmet.repository.NotificacaoPedidoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api/notificacoes")
public class AdminNotificacaoController {
    private final NotificacaoPedidoRepository notificacaoPedidoRepository;

    public AdminNotificacaoController(NotificacaoPedidoRepository notificacaoPedidoRepository) {
        this.notificacaoPedidoRepository = notificacaoPedidoRepository;
    }

    @GetMapping("/novas")
    public List<NotificacaoPedidoDTO> novas() {
        return notificacaoPedidoRepository.findByLidaFalseOrderByDataCriacaoDesc()
                .stream()
                .map(NotificacaoPedidoDTO::from)
                .toList();
    }
}
