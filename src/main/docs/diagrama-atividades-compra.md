# Diagrama de atividades — compra de cupcakes

```mermaid
flowchart TD
    A([Inicio]) --> B{Cliente autenticado?}
    B -- Nao --> C[Exibir login]
    C --> D{Login valido?}
    D -- Nao --> C
    D -- Sim --> E[Redirecionar cliente para o catalogo]
    B -- Sim --> E

    E --> F[Consultar catalogo de cupcakes]
    F --> G[Selecionar cupcake e quantidade]
    G --> H{Quantidade positiva?}
    H -- Nao --> I[Exibir erro de quantidade]
    I --> G
    H -- Sim --> J[Adicionar item ao carrinho da sessao]
    J --> K{Continuar comprando?}
    K -- Sim --> F
    K -- Nao --> L[Exibir carrinho]

    L --> M{Carrinho vazio?}
    M -- Sim --> F
    M -- Nao --> N[Atualizar ou remover itens]
    N --> O{Ir para checkout?}
    O -- Nao --> L
    O -- Sim --> P[Selecionar endereco de entrega]
    P --> Q[Selecionar forma e dados de pagamento]
    Q --> R{Dados validos?}
    R -- Nao --> S[Exibir erro no checkout]
    S --> Q
    R -- Sim --> T[Localizar cliente e endereco]
    T --> U{Cliente e endereco validos?}
    U -- Nao --> S
    U -- Sim --> V[Criar pedido e itens]
    V --> W[Calcular total e definir status EM_PREPARO]
    W --> X[Registrar pagamento aprovado]
    X --> Y[Registrar notificacao administrativa]
    Y --> Z[Limpar carrinho]
    Z --> AA[Exibir sucesso do pedido]
    AA --> AB([Fim])
```

As areas `/carrinho/**` e `/checkout/**` exigem a autoridade
`ROLE_CLIENTE`. A area administrativa, que recebe as notificacoes e gerencia
os pedidos, exige `ROLE_ADMIN`.

