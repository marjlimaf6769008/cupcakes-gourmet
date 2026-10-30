package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "administradores")
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String usuario;
    @Column(nullable = false)
    private String senha;

    protected Administrador() {
    }

    public Administrador(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public Long getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getSenha() { return senha; }
}
