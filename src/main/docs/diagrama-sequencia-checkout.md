# Diagrama de sequência — checkout

O fluxo abaixo representa a finalização de uma compra iniciada por um cliente
autenticado, desde o envio do formulário até a limpeza do carrinho.

```mermaid
sequenceDiagram
    actor Cliente
    participant CheckoutController
    participant CarrinhoSessao as Carrinho<br/>(sessao)
    participant CheckoutService
    participant ClienteRepository
    participant EnderecoRepository
    participant PedidoRepository
    participant PagamentoRepository
    participant NotificacaoRepository
    participant Banco as H2/JPA

    Cliente->>CheckoutController: POST /checkout/processar<br/>(formaPagamento, dadosPagamento, enderecoId)
    CheckoutController->>CarrinhoSessao: obter itens
    CheckoutController->>CheckoutService: finalizarCompra(carrinho, dados, clienteId, enderecoId)

    alt carrinho vazio
        CheckoutService-->>CheckoutController: IllegalStateException
        CheckoutController-->>Cliente: redirect /checkout com erro
    else dados de pagamento invalidos
        CheckoutService-->>CheckoutController: IllegalArgumentException
        CheckoutController-->>Cliente: redirect /checkout com erro
    else compra valida
        CheckoutService->>ClienteRepository: findById(clienteId)
        ClienteRepository->>Banco: SELECT cliente
        Banco-->>ClienteRepository: Cliente
        ClienteRepository-->>CheckoutService: Cliente

        CheckoutService->>EnderecoRepository: findByIdAndClienteId(enderecoId, clienteId)
        EnderecoRepository->>Banco: SELECT endereco do cliente
        Banco-->>EnderecoRepository: Endereco
        EnderecoRepository-->>CheckoutService: Endereco

        CheckoutService->>CheckoutService: criar Pedido e copiar endereco/contato
        CheckoutService->>PedidoRepository: save(pedido)
        PedidoRepository->>Banco: INSERT pedido e itens
        Banco-->>PedidoRepository: pedido com id
        PedidoRepository-->>CheckoutService: pedido salvo

        CheckoutService->>CheckoutService: criar Pagamento e aprovar()
        CheckoutService->>PagamentoRepository: save(pagamento)
        PagamentoRepository->>Banco: INSERT pagamento
        CheckoutService->>NotificacaoRepository: save(notificacao)
        NotificacaoRepository->>Banco: INSERT notificacao
        CheckoutService->>CarrinhoSessao: limparCarrinho()
        CheckoutService-->>CheckoutController: pedidoId
        CheckoutController-->>Cliente: redirect /checkout/sucesso/{id}
    end
```

Todas as operações de persistencia da finalizacao sao executadas pelo método
transacional `CheckoutService.finalizarCompra`.

