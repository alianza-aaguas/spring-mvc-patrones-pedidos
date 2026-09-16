package com.empresa.pedidos.model;

import com.empresa.pedidos.exception.EstadoInvalidoException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PATRON: Rich Domain Model (anti modelo anemico)
 * Rol: Agregado raiz. Las reglas de negocio (confirmar, aplicarDescuento,
 * calcularSubtotal) viven AQUI, no en el servicio. El servicio solo orquesta.
 *
 * PATRON: Builder
 * Rol: construccion legible de un objeto con varios campos, sin constructores
 * telescopicos y sin Lombok (codigo explicito con fines didacticos).
 */
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.BORRADOR;

    private BigDecimal total = BigDecimal.ZERO;

    private BigDecimal descuento = BigDecimal.ZERO;

    private String cupon;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    protected Pedido() { }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Regla de negocio: solo un BORRADOR con lineas puede confirmarse.
     * Lanza excepcion de dominio, que el @RestControllerAdvice traduce a HTTP.
     */
    public void confirmar() {
        if (estado != Estado.BORRADOR) {
            throw new EstadoInvalidoException("El pedido ya fue procesado: " + estado);
        }
        if (lineas.isEmpty()) {
            throw new EstadoInvalidoException("No se puede confirmar un pedido sin lineas");
        }
        this.estado = Estado.CONFIRMADO;
    }

    public void cancelar() {
        if (estado == Estado.CONFIRMADO) {
            throw new EstadoInvalidoException("Un pedido confirmado no se puede cancelar");
        }
        this.estado = Estado.CANCELADO;
    }

    /** Suma de los importes de las lineas, sin descuentos. */
    public BigDecimal calcularSubtotal() {
        return lineas.stream()
                .map(LineaPedido::importe)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** El total nunca baja de cero, por muchos descuentos que se acumulen. */
    public void aplicarDescuento(BigDecimal importe) {
        if (importe == null || importe.signum() <= 0) {
            return;
        }
        this.descuento = this.descuento.add(importe).setScale(2, RoundingMode.HALF_UP);
        this.total = calcularSubtotal().subtract(this.descuento).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void recalcularTotal() {
        this.total = calcularSubtotal().subtract(descuento).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int unidadesTotales() {
        return lineas.stream().mapToInt(LineaPedido::getCantidad).sum();
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<LineaPedido> getLineas() { return Collections.unmodifiableList(lineas); }
    public Estado getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getDescuento() { return descuento; }
    public String getCupon() { return cupon; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public static class Builder {
        private final Pedido p = new Pedido();

        public Builder cliente(Cliente cliente) { p.cliente = cliente; return this; }

        public Builder linea(LineaPedido linea) { p.lineas.add(linea); return this; }

        public Builder lineas(List<LineaPedido> lineas) { p.lineas.addAll(lineas); return this; }

        public Builder cupon(String cupon) { p.cupon = cupon; return this; }

        public Pedido build() {
            if (p.cliente == null) {
                throw new IllegalStateException("El pedido requiere cliente");
            }
            p.total = p.calcularSubtotal();
            return p;
        }
    }
}
