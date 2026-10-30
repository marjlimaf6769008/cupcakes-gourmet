# App Cupcakes Gourmet

Aplicacao web monolitica MVC para vitrine, cadastro de clientes e gestao de
pedidos de cupcakes gourmet. O projeto utiliza o pacote base
`br.com.cupcakesgourmet`.

## Stack

- Java 27 e Spring Boot 4.1.2;
- Spring MVC, Spring Data JPA e Bean Validation;
- Spring Security com autenticacao por formulario e BCrypt;
- Thymeleaf com Tailwind CSS via CDN;
- H2 em memoria, incluindo console web;
- JUnit 5, AssertJ e Mockito para testes.

## Funcionalidades

- catalogo publico de cupcakes;
- cadastro e autenticacao de clientes;
- carrinho de compras por sessao;
- checkout com endereco e simulacao de pagamento;
- consulta de pedidos, favoritos, perfil e enderecos do cliente;
- painel administrativo para cupcakes, pedidos, notificacoes e relatorios;
- endpoints JSON administrativos para atualizacao de pedidos e notificacoes.

## Estrutura

```text
src/main/java/br/com/cupcakesgourmet/
├── config/            # Configuracao de seguranca e carga inicial do admin
├── controller/        # Endpoints MVC e API administrativa
├── model/dto/         # Objetos de transferencia e formularios
├── model/entity/      # Entidades JPA e regras simples do dominio
├── model/session/     # Carrinho associado a sessao HTTP
├── repository/        # Interfaces Spring Data JPA
├── security/          # Principal e redirecionamento apos login
└── service/           # Regras de aplicacao
src/main/resources/
├── templates/         # Views Thymeleaf
├── static/            # Imagens e JavaScript
├── application.properties
└── data.sql           # Dados demonstrativos da vitrine
src/test/
├── java/              # Testes unitarios, MVC, JPA e integracao
└── resources/
    └── application-test.properties  # H2 isolado para testes
src/main/docs/
├── diagrama-classes.md
├── diagrama-sequencia-checkout.md
├── diagrama-atividades-compra.md
├── diagrama-casos-de-uso.md
├── historias-de-usuario.md
└── requisitos.md
```

## Execucao

Requisitos:

- JDK 27;
- acesso ao Maven Wrapper (`mvnw` ou `mvnw.cmd`).

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicacao inicia na porta `8080`. Acesse:

- catalogo: `http://localhost:8080/catalogo`;
- console H2: `http://localhost:8080/h2-console`.

No console H2, use:

```text
JDBC URL: jdbc:h2:mem:cupcakesdb
Usuario: sa
Senha: (vazia)
```

O ambiente principal usa `spring.jpa.hibernate.ddl-auto=update`, banco H2 em
memoria e `data.sql` para carregar os cupcakes demonstrativos. Como o banco e
em memoria, os dados sao perdidos quando a aplicacao e encerrada.

## Autenticacao e autorizacao

As rotas `/`, `/catalogo`, `/login`, `/registrar`, arquivos estaticos e
`/h2-console` sao publicas. As rotas `/admin/**` exigem a autoridade
`ROLE_ADMIN`; `/carrinho/**`, `/checkout/**` e `/cliente/**` exigem
`ROLE_CLIENTE`.

Em um ambiente vazio, o `AdminDataInitializer` cria o administrador
demonstrativo abaixo:

```text
E-mail: admin@cupcakes.com
Senha: admin123
```

Essas credenciais existem apenas para demonstracao local e devem ser alteradas
ou removidas antes de qualquer implantacao real.

## Testes

Execute toda a suite:

Linux/macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

Os testes utilizam JUnit 5, AssertJ e Mockito. O perfil `test` usa um banco H2
separado (`jdbc:h2:mem:cupcakes-test`) com `create-drop` e
`spring.sql.init.mode=never`, portanto nao executa o `data.sql` de producao.
Isso evita que os dados demonstrativos interfiram nos cenarios automatizados.

A cobertura atual inclui:

- regras de negocio do carrinho e do checkout;
- carregamento de usuarios e autoridades do Spring Security;
- controllers MVC de catalogo e registro;
- regras de autorizacao para areas publicas e administrativas;
- persistencia de cupcakes e usuarios com Spring Data JPA;
- inicializacao do contexto Spring com MockMvc e H2.

## Diagramas

Os diagramas da arquitetura e dos principais fluxos estão em
`src/main/docs/` no formato Mermaid:

- `diagrama-classes.md`: classes de domínio, controllers, serviços e
  repositórios;
- `diagrama-sequencia-checkout.md`: sequência de finalização da compra;
- `diagrama-atividades-compra.md`: atividades do fluxo de compra e suas
  validações.
- `diagrama-casos-de-uso.md`: atores, funcionalidades e regras de acesso do
  sistema.
- `historias-de-usuario.md`: histórias, prioridades e critérios de aceitação
  para visitantes, clientes e administradores.
- `requisitos.md`: requisitos funcionais e não funcionais consolidados do
  sistema.

Os arquivos podem ser visualizados diretamente em ferramentas compatíveis com
Mermaid, como o GitHub ou o Mermaid Live Editor.
