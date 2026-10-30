package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "primeiro_nome", nullable = false, length = 80)
    private String primeiroNome;

    @Column(nullable = false, length = 120)
    private String sobrenome;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false, length = 15)
    private String telefone;

    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Carrinho carrinho;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorito> favoritos = new ArrayList<>();

    protected Cliente() {
    }

    public Cliente(String primeiroNome, String sobrenome, String cpf,
                   LocalDate dataNascimento, String telefone,
                   String email, String senha) {
        this.primeiroNome = primeiroNome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
    }

    public Long getId() { return id; }
    public String getPrimeiroNome() { return primeiroNome; }
    public String getSobrenome() { return sobrenome; }
    public String getNomeCompleto() { return primeiroNome + " " + sobrenome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getCpf() { return cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }
    public List<Pedido> getPedidos() { return pedidos; }
    public Carrinho getCarrinho() { return carrinho; }
    public List<Favorito> getFavoritos() { return favoritos; }

    public void atualizarPerfil(String primeiroNome, String sobrenome, String cpf,
                                LocalDate dataNascimento, String telefone) {
        this.primeiroNome = primeiroNome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

    public Cliente(String nome, String email, String senha) {
        this(nome.trim().split("\\s+", 2)[0],
                nome.trim().contains(" ") ? nome.trim().substring(nome.trim().indexOf(' ') + 1) : "Cliente",
                "000.000.000-00", LocalDate.of(1900, 1, 1), "(00) 00000-0000",
                email, senha);
    }
}
