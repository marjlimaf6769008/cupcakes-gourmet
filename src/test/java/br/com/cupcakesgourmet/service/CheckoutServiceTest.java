package br.com.cupcakesgourmet.service;

import br.com.cupcakesgourmet.model.entity.Cliente;
import br.com.cupcakesgourmet.model.entity.Pagamento;
import br.com.cupcakesgourmet.model.entity.Pedido;
import br.com.cupcakesgourmet.model.entity.Cupcake;
import br.com.cupcakesgourmet.model.entity.Endereco;
import br.com.cupcakesgourmet.model.session.Carrinho;
import br.com.cupcakesgourmet.repository.ClienteRepository;
import br.com.cupcakesgourmet.repository.PagamentoRepository;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import br.com.cupcakesgourmet.repository.NotificacaoPedidoRepository;
import br.com.cupcakesgourmet.repository.EnderecoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private NotificacaoPedidoRepository notificacaoPedidoRepository;
    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    @DisplayName("deve criar pedido, aprovar pagamento e limpar o carrinho")
    void deveFinalizarCompra() throws Exception {
        Cliente cliente = new Cliente("Maria", "maria@example.com", "hash");
        Cupcake cupcake = cupcake(1L);
        Carrinho carrinho = new Carrinho();
        carrinho.adicionarCupcake(cupcake, 2);
        when(clienteRepository.findById(7L)).thenReturn(Optional.of(cliente));
        Endereco endereco = endereco(cliente);
        setId(endereco, 8L);
        when(enderecoRepository.findByIdAndClienteId(8L, 7L)).thenReturn(Optional.of(endereco));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            setId(pedido, 42L);
            return pedido;
        });

        Long pedidoId = checkoutService.finalizarCompra(carrinho, "PIX", "chave-ficticia", 7L, 8L);

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(pedidoCaptor.capture());
        Pedido pedido = pedidoCaptor.getValue();
        assertThat(pedidoId).isEqualTo(42L);
        assertThat(pedido.getStatus()).isEqualTo(Pedido.Status.EM_PREPARO);
        assertThat(pedido.getCliente()).isSameAs(cliente);
        assertThat(pedido.getItens()).hasSize(1);
        assertThat(pedido.getItens().get(0).getQuantidade()).isEqualTo(2);
        assertThat(pedido.getValorTotal()).isEqualByComparingTo("25.00");
        verify(pagamentoRepository).save(argThat(pagamento ->
                pagamento.getTipo() == Pagamento.Tipo.PIX
                        && pagamento.getStatus() == Pagamento.Status.APROVADO
                        && pagamento.getPedido() == pedido));
        verify(notificacaoPedidoRepository).save(argThat(notificacao ->
                notificacao.getPedidoId().equals(42L) && !notificacao.isLida()));
        assertThat(carrinho.getItens()).isEmpty();
    }

    @Test
    @DisplayName("deve rejeitar finalização de carrinho vazio")
    void deveRejeitarCarrinhoVazio() {
        assertThatThrownBy(() -> checkoutService.finalizarCompra(
                new Carrinho(), "PIX", "chave-ficticia", 7L, 8L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não é possível finalizar um carrinho vazio.");

        verifyNoInteractions(clienteRepository, pedidoRepository, pagamentoRepository,
                notificacaoPedidoRepository, enderecoRepository);
    }

    @Test
    @DisplayName("deve rejeitar cartão com número insuficiente")
    void deveRejeitarCartaoInvalido() throws Exception {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionarCupcake(cupcake(1L), 1);

        assertThatThrownBy(() -> checkoutService.finalizarCompra(
                carrinho, "cartao", "12345678901", 7L, 8L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Informe um número de cartão válido para a simulação.");

        verifyNoInteractions(clienteRepository, pedidoRepository, pagamentoRepository,
                notificacaoPedidoRepository, enderecoRepository);
    }

    @Test
    @DisplayName("deve rejeitar forma de pagamento desconhecida")
    void deveRejeitarFormaDePagamentoDesconhecida() throws Exception {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionarCupcake(cupcake(1L), 1);

        assertThatThrownBy(() -> checkoutService.finalizarCompra(
                carrinho, "boleto", "dados", 7L, 8L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Forma de pagamento inválida.");
    }

    private Cupcake cupcake(Long id) throws Exception {
        Cupcake cupcake = new Cupcake("Red Velvet", "Descrição", new BigDecimal("12.50"), null);
        Field field = Cupcake.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(cupcake, id);
        return cupcake;
    }

    private void setId(Pedido pedido, Long id) throws Exception {
        Field field = Pedido.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(pedido, id);
    }

    private Endereco endereco(Cliente cliente) {
        Endereco endereco = new Endereco(cliente);
        endereco.setCep("01001-000");
        endereco.setLogradouro("Praça da Sé");
        endereco.setNumero("1");
        endereco.setBairro("Sé");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");
        endereco.setNomeDestinatario("Maria");
        return endereco;
    }

    private void setId(Endereco endereco, Long id) throws Exception {
        Field field = Endereco.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(endereco, id);
    }
}
