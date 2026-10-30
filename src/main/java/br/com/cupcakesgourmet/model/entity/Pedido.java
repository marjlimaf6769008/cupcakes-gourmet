package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    public enum Status { AGUARDANDO_PAGAMENTO, EM_PREPARO, PRONTO, EM_ENTREGA, FINALIZADO, CONCLUIDO, CANCELADO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "logradouro_entrega", length = 180)
    private String logradouroEntrega;

    @Column(name = "numero_entrega", length = 30)
    private String numeroEntrega;

    @Column(name = "complemento_entrega", length = 100)
    private String complementoEntrega;

    @Column(name = "bairro_entrega", length = 100)
    private String bairroEntrega;

    @Column(name = "cidade_entrega", length = 100)
    private String cidadeEntrega;

    @Column(name = "uf_entrega", length = 2)
    private String ufEntrega;

    @Column(name = "cep_entrega", length = 9)
    private String cepEntrega;

    @Column(name = "nome_destinatario", length = 120)
    private String nomeDestinatario;

    @Column(name = "cpf_cliente", length = 14)
    private String cpfCliente;

    @Column(name = "telefone_cliente", length = 15)
    private String telefoneCliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.data = LocalDateTime.now();
        this.status = Status.AGUARDANDO_PAGAMENTO;
    }

    public void adicionarItem(Cupcake cupcake, int quantidade) {
        itens.add(new ItemPedido(this, cupcake, quantidade));
        recalcularTotal();
    }

    public void recalcularTotal() {
        valorTotal = itens.stream().map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public LocalDateTime getData() { return data; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getLogradouroEntrega() { return logradouroEntrega; }
    public String getNumeroEntrega() { return numeroEntrega; }
    public String getComplementoEntrega() { return complementoEntrega; }
    public String getBairroEntrega() { return bairroEntrega; }
    public String getCidadeEntrega() { return cidadeEntrega; }
    public String getUfEntrega() { return ufEntrega; }
    public String getCepEntrega() { return cepEntrega; }
    public String getNomeDestinatario() { return nomeDestinatario; }
    public String getCpfCliente() { return cpfCliente; }
    public String getTelefoneCliente() { return telefoneCliente; }
    public void definirEnderecoEntrega(Endereco endereco) {
        this.logradouroEntrega = endereco.getLogradouro();
        this.numeroEntrega = endereco.getNumero();
        this.complementoEntrega = endereco.getComplemento();
        this.bairroEntrega = endereco.getBairro();
        this.cidadeEntrega = endereco.getCidade();
        this.ufEntrega = endereco.getUf();
        this.cepEntrega = endereco.getCep();
        this.nomeDestinatario = endereco.getNomeDestinatario();
    }
    public void definirDadosContato(Cliente cliente) {
        this.cpfCliente = cliente.getCpf();
        this.telefoneCliente = cliente.getTelefone();
    }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return itens; }
}
