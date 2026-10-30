package br.com.cupcakesgourmet.service;

import br.com.cupcakesgourmet.model.dto.ItemCarrinhoDTO;
import br.com.cupcakesgourmet.model.entity.Cliente;
import br.com.cupcakesgourmet.model.entity.Endereco;
import br.com.cupcakesgourmet.model.entity.Pagamento;
import br.com.cupcakesgourmet.model.entity.Pedido;
import br.com.cupcakesgourmet.model.entity.NotificacaoPedido;
import br.com.cupcakesgourmet.model.session.Carrinho;
import br.com.cupcakesgourmet.repository.ClienteRepository;
import br.com.cupcakesgourmet.repository.NotificacaoPedidoRepository;
import br.com.cupcakesgourmet.repository.EnderecoRepository;
import br.com.cupcakesgourmet.repository.PagamentoRepository;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CheckoutService {
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final NotificacaoPedidoRepository notificacaoPedidoRepository;
    private final EnderecoRepository enderecoRepository;

    public CheckoutService(ClienteRepository clienteRepository,
                           PedidoRepository pedidoRepository,
                           PagamentoRepository pagamentoRepository,
                           NotificacaoPedidoRepository notificacaoPedidoRepository,
                           EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.notificacaoPedidoRepository = notificacaoPedidoRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional
    public Long finalizarCompra(Carrinho carrinho, String formaPagamento,
                                String dadosPagamento, Long clienteId, Long enderecoId) {
        if (carrinho.getItens().isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar um carrinho vazio.");
        }

        Pagamento.Tipo tipo = converterTipoPagamento(formaPagamento);
        validarDadosPagamento(tipo, dadosPagamento);
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        if (enderecoId == null) {
            throw new IllegalArgumentException("Selecione um endereço de entrega.");
        }
        Endereco endereco = enderecoRepository.findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Selecione um endereço de entrega válido."));

        Pedido pedido = new Pedido(cliente);
        pedido.definirEnderecoEntrega(endereco);
        pedido.definirDadosContato(cliente);
        for (ItemCarrinhoDTO item : carrinho.getItens()) {
            pedido.adicionarItem(item.getCupcake(), item.getQuantidade());
        }
        pedido.setStatus(Pedido.Status.EM_PREPARO);
        pedido = pedidoRepository.save(pedido);

        Pagamento pagamento = new Pagamento(tipo, pedido);
        pagamento.aprovar();
        pagamentoRepository.save(pagamento);
        notificacaoPedidoRepository.save(new NotificacaoPedido(
                pedido.getId(), "Novo pedido #" + pedido.getId() + " aguardando preparo."));
        carrinho.limparCarrinho();
        return pedido.getId();
    }

    private Pagamento.Tipo converterTipoPagamento(String formaPagamento) {
        if (formaPagamento == null) {
            throw new IllegalArgumentException("Selecione uma forma de pagamento.");
        }
        try {
            return Pagamento.Tipo.valueOf(formaPagamento.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Forma de pagamento inválida.");
        }
    }

    private void validarDadosPagamento(Pagamento.Tipo tipo, String dadosPagamento) {
        if (dadosPagamento == null || dadosPagamento.isBlank()) {
            throw new IllegalArgumentException("Informe os dados do pagamento.");
        }
        if (tipo == Pagamento.Tipo.CARTAO && dadosPagamento.replaceAll("\\D", "").length() < 12) {
            throw new IllegalArgumentException("Informe um número de cartão válido para a simulação.");
        }
    }
}
