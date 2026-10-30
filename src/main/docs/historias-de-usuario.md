# Histórias de usuário

Este documento descreve as principais necessidades dos usuários da aplicação
Cupcakes Gourmet e seus critérios de aceitação. As histórias estão agrupadas
por perfil de acesso e priorizadas para apoiar o planejamento do projeto.

## Perfis

- **Visitante:** pessoa que acessa a aplicação sem autenticação.
- **Cliente:** usuário autenticado com a autoridade `ROLE_CLIENTE`.
- **Administrador:** usuário autenticado com a autoridade `ROLE_ADMIN`.

## Visitante

### HU-01 — Consultar o catálogo

**Como** visitante, **quero** visualizar os cupcakes disponíveis, **para**
conhecer os produtos e seus preços antes de comprar.

**Prioridade:** Alta

**Critérios de aceitação:**

- O catálogo deve estar disponível sem autenticação.
- Cada produto deve apresentar nome, descrição, preço e imagem quando houver.
- A aplicação deve exibir uma mensagem adequada caso não existam produtos.

### HU-02 — Acessar a página de login

**Como** visitante, **quero** acessar a tela de login, **para** entrar na
minha conta.

**Prioridade:** Alta

**Critérios de aceitação:**

- A página deve estar disponível no endereço `/login`.
- O formulário deve permitir informar e-mail e senha.
- Credenciais inválidas devem manter o usuário no fluxo de autenticação com
  indicação de erro.

### HU-03 — Criar uma conta de cliente

**Como** visitante, **quero** me cadastrar, **para** realizar compras e
acompanhar meus pedidos.

**Prioridade:** Alta

**Critérios de aceitação:**

- O cadastro deve validar nome, sobrenome, CPF, data de nascimento, telefone,
  e-mail e senha.
- A confirmação da senha deve ser igual à senha informada.
- E-mail e CPF não podem estar previamente cadastrados.
- O e-mail deve ser normalizado antes da gravação.
- A senha deve ser armazenada codificada, nunca em texto puro.
- Após o cadastro válido, o usuário deve ser direcionado para o login.

## Cliente

### HU-04 — Autenticar-se como cliente

**Como** cliente, **quero** entrar na aplicação, **para** acessar os recursos
de compra e acompanhamento.

**Prioridade:** Alta

**Critérios de aceitação:**

- Um cliente autenticado deve receber a autoridade `ROLE_CLIENTE`.
- Após o login, o cliente deve ser direcionado para o catálogo.
- A sessão deve manter a identificação do cliente para operações de pedido.
- Rotas de cliente devem exigir autenticação e a autoridade correta.

### HU-05 — Gerenciar o carrinho

**Como** cliente, **quero** adicionar, atualizar e remover cupcakes do
carrinho, **para** montar minha compra.

**Prioridade:** Alta

**Critérios de aceitação:**

- A quantidade adicionada deve ser maior que zero.
- Adicionar o mesmo cupcake deve acumular sua quantidade.
- O cliente deve conseguir alterar a quantidade de um item.
- Quantidade zero ou negativa deve remover o item ou ser rejeitada conforme o
  fluxo utilizado.
- O subtotal e o total geral devem ser recalculados corretamente.
- O carrinho deve permanecer associado à sessão do cliente.

### HU-06 — Finalizar uma compra

**Como** cliente, **quero** concluir o checkout, **para** registrar meu pedido
e receber os cupcakes.

**Prioridade:** Alta

**Critérios de aceitação:**

- Não deve ser possível finalizar um carrinho vazio.
- O cliente deve selecionar um endereço pertencente a ele.
- O cliente deve informar uma forma de pagamento válida.
- Dados obrigatórios de pagamento devem ser validados.
- Para cartão, o número informado deve atender à quantidade mínima de dígitos
  definida pela aplicação.
- O pedido deve ser criado com seus itens, endereço, contato e valor total.
- O pedido deve ser criado com status `EM_PREPARO`.
- O pagamento simulado deve ser registrado como `APROVADO`.
- Uma notificação administrativa deve ser criada.
- O carrinho deve ser limpo após a conclusão.
- O cliente deve ser direcionado à página de sucesso do pedido.

### HU-07 — Consultar meus pedidos

**Como** cliente, **quero** visualizar meus pedidos, **para** acompanhar meu
histórico de compras.

**Prioridade:** Alta

**Critérios de aceitação:**

- A lista deve exibir somente pedidos do cliente autenticado.
- Cada pedido deve apresentar identificação, data, status e valor total.
- O cliente deve conseguir acessar os detalhes de um pedido próprio.
- Um cliente não pode consultar detalhes de pedido pertencente a outro
  cliente.

### HU-08 — Acompanhar o status do pedido

**Como** cliente, **quero** consultar o status dos meus pedidos, **para**
acompanhar o andamento da entrega.

**Prioridade:** Média

**Critérios de aceitação:**

- O status deve refletir o estado persistido do pedido.
- A consulta deve respeitar o escopo do cliente autenticado.
- Pedidos inexistentes ou de outro cliente devem resultar em acesso negado ou
  recurso não encontrado, conforme o endpoint.

### HU-09 — Gerenciar endereços de entrega

**Como** cliente, **quero** cadastrar, editar e remover endereços, **para**
escolher onde receber meus pedidos.

**Prioridade:** Alta

**Critérios de aceitação:**

- O endereço deve validar CEP, logradouro, número, bairro, cidade e UF.
- O cliente deve visualizar somente seus próprios endereços.
- Um endereço de outro cliente não pode ser utilizado no checkout.
- O cliente deve conseguir excluir um endereço autorizado.

### HU-10 — Gerenciar favoritos

**Como** cliente, **quero** favoritar cupcakes, **para** encontrá-los
facilmente em uma próxima visita.

**Prioridade:** Média

**Critérios de aceitação:**

- O cliente deve conseguir adicionar um cupcake aos favoritos.
- O cliente deve visualizar somente seus próprios favoritos.
- O cliente deve conseguir remover um favorito.
- A mesma combinação cliente-cupcake não deve gerar duplicidade indevida.

### HU-11 — Atualizar meu perfil

**Como** cliente, **quero** atualizar meus dados pessoais, **para** manter
meu cadastro correto.

**Prioridade:** Média

**Critérios de aceitação:**

- O cliente deve conseguir alterar nome, sobrenome, CPF, data de nascimento e
  telefone.
- Os campos devem respeitar as validações definidas pela aplicação.
- A alteração deve atingir somente o perfil do cliente autenticado.

### HU-12 — Encerrar a sessão

**Como** cliente, **quero** sair da aplicação, **para** proteger minha conta
em dispositivos compartilhados.

**Prioridade:** Alta

**Critérios de aceitação:**

- O logout deve invalidar a sessão HTTP.
- O cookie `JSESSIONID` deve ser removido.
- O usuário deve ser redirecionado para `/login?logout`.

## Administrador

### HU-13 — Autenticar-se como administrador

**Como** administrador, **quero** entrar no painel administrativo, **para**
gerenciar a operação da loja.

**Prioridade:** Alta

**Critérios de aceitação:**

- O administrador deve receber a autoridade `ROLE_ADMIN`.
- Após o login, deve ser direcionado para `/admin/cupcakes`.
- As rotas administrativas devem rejeitar visitantes e clientes comuns.

### HU-14 — Gerenciar cupcakes

**Como** administrador, **quero** cadastrar, editar e remover cupcakes,
**para** manter o catálogo atualizado.

**Prioridade:** Alta

**Critérios de aceitação:**

- O cadastro deve validar nome, descrição e preço positivo.
- O administrador deve conseguir informar ou atualizar a URL da foto.
- O catálogo deve refletir as alterações persistidas.
- Usuários sem `ROLE_ADMIN` não podem executar essas operações.

### HU-15 — Gerenciar pedidos

**Como** administrador, **quero** consultar pedidos e atualizar seus status,
**para** controlar a preparação e a entrega.

**Prioridade:** Alta

**Critérios de aceitação:**

- O administrador deve visualizar pedidos com dados do cliente e itens.
- Deve ser possível consultar os detalhes de um pedido.
- Deve ser possível atualizar o status do pedido.
- A alteração deve ser persistida e refletida nas consultas do cliente.

### HU-16 — Consultar notificações de pedidos

**Como** administrador, **quero** consultar novas notificações, **para**
identificar pedidos aguardando atendimento.

**Prioridade:** Alta

**Critérios de aceitação:**

- O sistema deve gerar uma notificação quando um pedido for finalizado.
- A notificação deve identificar o pedido e informar que ele aguarda preparo.
- O administrador deve conseguir consultar as notificações disponíveis por
  endpoint administrativo.

### HU-17 — Consultar relatórios de vendas

**Como** administrador, **quero** consultar relatórios de vendas, **para**
acompanhar o desempenho da loja.

**Prioridade:** Média

**Critérios de aceitação:**

- O relatório deve permitir consultar vendas por intervalo de datas.
- Pedidos cancelados não devem compor o total de vendas.
- O relatório deve apresentar quantidade de pedidos e soma dos valores.
- O acesso deve ser restrito a `ROLE_ADMIN`.

## Requisitos transversais

### RT-01 — Segurança de acesso

Todos os recursos protegidos devem validar autenticação e autoridade antes de
executar a operação. Dados de clientes e pedidos não podem ser expostos a
outros usuários.

### RT-02 — Persistência consistente

A finalização do checkout deve ser transacional: pedido, pagamento e
notificação devem ser persistidos de forma consistente, e o carrinho só deve
ser limpo após a criação bem-sucedida do pedido.

### RT-03 — Testabilidade

As regras de negócio devem possuir testes unitários com JUnit 5, AssertJ e
Mockito. Os fluxos de persistência e autorização devem possuir testes de
integração com Spring Boot, MockMvc e H2 isolado pelo perfil `test`.

