# Requisitos do sistema

Este documento consolida os requisitos funcionais e não funcionais da
aplicação Cupcakes Gourmet. Os requisitos foram derivados das funcionalidades
implementadas no projeto, dos perfis de acesso e dos fluxos documentados nas
histórias de usuário.

## 1. Perfis de usuário

- **Visitante:** acessa recursos públicos sem autenticação.
- **Cliente:** usuário autenticado com a autoridade `ROLE_CLIENTE`.
- **Administrador:** usuário autenticado com a autoridade `ROLE_ADMIN`.

## 2. Requisitos funcionais

### RF-01 — Exibir catálogo

O sistema deve permitir que visitantes e usuários autenticados consultem o
catálogo de cupcakes com nome, descrição, preço e imagem, quando disponível.

### RF-02 — Permitir cadastro de cliente

O sistema deve permitir o cadastro de clientes informando nome, sobrenome,
CPF, data de nascimento, telefone, e-mail, senha e confirmação de senha.

### RF-03 — Validar cadastro

O sistema deve validar campos obrigatórios, formatos de CPF, telefone e
e-mail, tamanho da senha e igualdade entre senha e confirmação.

### RF-04 — Impedir duplicidade de cadastro

O sistema não deve permitir o cadastro de e-mail ou CPF já existente. O
e-mail deve ser normalizado antes da consulta e persistência.

### RF-05 — Proteger credenciais

O sistema deve armazenar senhas utilizando codificação segura com BCrypt,
nunca em texto puro.

### RF-06 — Autenticar usuários

O sistema deve autenticar usuários por e-mail e senha utilizando Spring
Security e redirecioná-los de acordo com o perfil:

- administrador para `/admin/cupcakes`;
- cliente para `/catalogo`.

### RF-07 — Gerenciar carrinho

O cliente deve poder adicionar cupcakes ao carrinho, atualizar quantidades,
remover itens e visualizar o total da compra.

### RF-08 — Validar quantidade de itens

O sistema deve aceitar somente quantidades positivas ao adicionar itens. A
atualização para quantidade zero ou negativa deve remover o item conforme a
regra do carrinho.

### RF-09 — Calcular valores

O sistema deve calcular o subtotal de cada item e o valor total do carrinho e
do pedido com base no preço e na quantidade.

### RF-10 — Gerenciar endereços

O cliente deve poder listar, cadastrar, atualizar e remover seus endereços de
entrega.

### RF-11 — Restringir endereços por cliente

O sistema deve permitir que um cliente utilize e visualize somente endereços
associados à sua própria conta.

### RF-12 — Processar checkout

O sistema deve permitir que o cliente autenticado informe endereço e forma de
pagamento para finalizar uma compra.

### RF-13 — Validar checkout

O sistema deve rejeitar checkout quando:

- o carrinho estiver vazio;
- a forma de pagamento for ausente ou inválida;
- os dados de pagamento estiverem ausentes;
- os dados de cartão não atenderem ao mínimo de dígitos;
- o endereço não for informado, não existir ou não pertencer ao cliente.

### RF-14 — Criar pedido

Ao concluir um checkout válido, o sistema deve criar um pedido com cliente,
itens, quantidades, endereço, dados de contato, valor total e status
`EM_PREPARO`.

### RF-15 — Registrar pagamento

O sistema deve criar o pagamento associado ao pedido e registrá-lo como
`APROVADO` após a validação da simulação.

### RF-16 — Gerar notificação

O sistema deve criar uma notificação administrativa para cada novo pedido
finalizado, identificando o pedido e informando que ele aguarda preparo.

### RF-17 — Limpar carrinho após compra

O sistema deve limpar o carrinho da sessão somente após a criação bem-sucedida
do pedido, pagamento e notificação.

### RF-18 — Consultar pedidos do cliente

O cliente deve poder consultar sua lista de pedidos, detalhes e status, sem
acessar pedidos pertencentes a outros clientes.

### RF-19 — Gerenciar favoritos

O cliente deve poder adicionar, consultar e remover cupcakes favoritos.

### RF-20 — Atualizar perfil

O cliente deve poder atualizar nome, sobrenome, CPF, data de nascimento e
telefone, respeitando as validações aplicáveis.

### RF-21 — Encerrar sessão

O sistema deve permitir logout, invalidar a sessão HTTP, remover o cookie
`JSESSIONID` e redirecionar o usuário para `/login?logout`.

### RF-22 — Gerenciar cupcakes

O administrador deve poder listar, cadastrar, editar e remover cupcakes,
validando nome, descrição, preço positivo e URL da foto quando informada.

### RF-23 — Gerenciar pedidos administrativamente

O administrador deve poder consultar pedidos, visualizar detalhes e atualizar
seus status.

### RF-24 — Consultar notificações administrativas

O administrador deve poder consultar novas notificações de pedidos por meio da
área administrativa e de seu endpoint de API.

### RF-25 — Consultar relatórios de vendas

O administrador deve poder consultar relatórios por intervalo de datas,
incluindo quantidade de pedidos e soma dos valores. Pedidos cancelados não
devem compor os totais de vendas.

### RF-26 — Disponibilizar API administrativa

O sistema deve disponibilizar endpoints administrativos para consulta de
notificações e pedidos atualizados, protegidos pela autoridade
`ROLE_ADMIN`.

### RF-27 — Carregar dados iniciais

O sistema deve carregar os cupcakes demonstrativos definidos em `data.sql` e
criar o administrador inicial quando ele ainda não existir no banco.

## 3. Requisitos não funcionais

### RNF-01 — Arquitetura

O sistema deve seguir arquitetura MVC, separando controllers, serviços,
entidades/DTOs, repositórios, configuração e componentes de segurança.

### RNF-02 — Plataforma

O sistema deve ser executável com Java 27, Spring Boot 4.1.2 e Maven Wrapper.

### RNF-03 — Persistência

O sistema deve utilizar Spring Data JPA para persistência e H2 em memória como
banco de dados do ambiente de desenvolvimento.

### RNF-04 — Banco de testes isolado

Os testes devem utilizar o perfil `test`, banco H2 separado
(`jdbc:h2:mem:cupcakes-test`) e `create-drop`, sem executar o `data.sql` de
produção.

### RNF-05 — Integridade transacional

A finalização do checkout deve ser transacional, mantendo consistentes as
operações de pedido, itens, pagamento e notificação.

### RNF-06 — Segurança de autorização

O sistema deve restringir rotas conforme as autoridades:

- recursos públicos: `/`, `/catalogo`, `/login`, `/registrar` e arquivos
  estáticos;
- cliente: `/carrinho/**`, `/checkout/**` e `/cliente/**`;
- administrador: `/admin/**`.

### RNF-07 — Segurança de sessão

O carrinho deve ser associado à sessão HTTP do cliente, e o logout deve
invalidar a sessão e remover o cookie de sessão.

### RNF-08 — Validação de entrada

Dados recebidos por formulários e endpoints devem ser validados com Jakarta
Bean Validation e regras de negócio específicas antes da persistência.

### RNF-09 — Privacidade de dados

O sistema não deve expor dados de pedidos, endereços, favoritos ou perfis de
um cliente para outro cliente.

### RNF-10 — Interface web

As páginas devem ser renderizadas com Thymeleaf e disponibilizadas como
templates MVC, com recursos estáticos separados.

### RNF-11 — Disponibilidade do serviço

A aplicação deve iniciar na porta `8080` por padrão e disponibilizar o
catálogo sem autenticação.

### RNF-12 — Configuração por ambiente

As configurações de produção/desenvolvimento e testes devem ser separadas por
arquivos de propriedades, evitando conflito entre o banco principal e o banco
de testes.

### RNF-13 — Testabilidade

As regras de negócio devem possuir testes unitários com JUnit 5, AssertJ e
Mockito. Os fluxos MVC, persistência e segurança devem possuir testes de
integração com Spring Boot, MockMvc e H2.

### RNF-14 — Manutenibilidade

O código deve manter responsabilidades separadas, nomes expressivos,
validações explícitas e documentação dos principais fluxos em Mermaid e
Markdown.

### RNF-15 — Observabilidade de falhas

Erros de validação, autenticação, autorização e processamento de checkout
devem ser tratados explicitamente, com retorno adequado ao usuário e sem
falhas silenciosas.

### RNF-16 — Dados demonstrativos

As credenciais e dados iniciais existentes no projeto devem ser considerados
somente para desenvolvimento local e não devem ser utilizados em produção sem
alteração.

