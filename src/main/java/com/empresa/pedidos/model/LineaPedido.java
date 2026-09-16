package com.empresa.pedidos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

/**
 * PATRON: Rich Domain Model
 * Rol: la linea sabe calcular su propio importe. El servicio no hace aritmetica.
 */
@Entity
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Producto producto;

    private int cantidad;

    protected LineaPedido() { }

    public LineaPedido(Producto producto, int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /** Regla de negocio local: importe de la linea. */
    public BigDecimal importe() {
        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    public Long getId() { return id; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
}
