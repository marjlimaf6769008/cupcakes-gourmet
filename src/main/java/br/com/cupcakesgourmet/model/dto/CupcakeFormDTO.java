package br.com.cupcakesgourmet.model.dto;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CupcakeFormDTO {
    private Long id;

    @NotBlank(message = "Informe o nome do cupcake.")
    @Size(min = 3, message = "O nome deve ter pelo menos 3 caracteres.")
    private String nome;

    @NotBlank(message = "Informe a descrição do cupcake.")
    private String descricao;

    @NotNull(message = "Informe o preço do cupcake.")
    @Positive(message = "O preço deve ser maior que zero.")
    private BigDecimal preco;

    private String fotoUrl;

    public CupcakeFormDTO() {
    }

    public CupcakeFormDTO(Cupcake cupcake) {
        this.id = cupcake.getId();
        this.nome = cupcake.getNome();
        this.descricao = cupcake.getDescricao();
        this.preco = cupcake.getPreco();
        this.fotoUrl = cupcake.getFotoUrl();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
