package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class Usuario {
    public enum Papel { ROLE_CLIENTE, ROLE_ADMIN }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 80)
    @Column(name = "primeiro_nome", nullable = false, length = 80)
    private String primeiroNome;

    @NotBlank
    @Size(min = 2, max = 120)
    @Column(nullable = false, length = 120)
    private String sobrenome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false, length = 100)
    private String senha;

    @NotBlank
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @NotNull
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @NotBlank
    @Pattern(regexp = "\\(\\d{2}\\) \\d{5}-\\d{4}")
    @Column(nullable = false, length = 15)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Papel papel;

    protected Usuario() {
    }

    public Usuario(String primeiroNome, String sobrenome, String cpf,
                   LocalDate dataNascimento, String telefone, String email,
                   String senha, Papel papel) {
        this.primeiroNome = primeiroNome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.papel = papel;
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
    public Papel getPapel() { return papel; }

    public void atualizarPerfil(String primeiroNome, String sobrenome, String cpf,
                                LocalDate dataNascimento, String telefone) {
        this.primeiroNome = primeiroNome;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

    /**
     * Compatibilidade com dados administrativos antigos que ainda informam um nome único.
     */
    public Usuario(String nome, String email, String senha, Papel papel) {
        this(nome.trim().split("\\s+", 2)[0],
                nome.trim().contains(" ") ? nome.trim().substring(nome.trim().indexOf(' ') + 1) : "Admin",
                "000.000.000-00", LocalDate.of(1900, 1, 1), "(00) 00000-0000",
                email, senha, papel);
    }
}
