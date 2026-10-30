package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "cupcakes")
public class Cupcake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do cupcake.")
    @Size(min = 3, message = "O nome deve ter pelo menos 3 caracteres.")
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Informe a descrição do cupcake.")
    @Column(nullable = false, length = 500)
    private String descricao;

    @NotNull(message = "Informe o preço do cupcake.")
    @Positive(message = "O preço deve ser maior que zero.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    protected Cupcake() {
    }

    public Cupcake(String nome, String descricao, BigDecimal preco, String fotoUrl) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
