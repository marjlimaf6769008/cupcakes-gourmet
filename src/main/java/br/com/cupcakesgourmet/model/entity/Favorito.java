package br.com.cupcakesgourmet.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favoritos",
        uniqueConstraints = @UniqueConstraint(name = "uk_favorito_cliente_cupcake",
                columnNames = {"cliente_id", "cupcake_id"}))
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cupcake_id", nullable = false)
    private Cupcake cupcake;

    protected Favorito() {
    }

    public Favorito(Cliente cliente, Cupcake cupcake) {
        this.cliente = cliente;
        this.cupcake = cupcake;
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Cupcake getCupcake() { return cupcake; }
}
