package com.empresa.pedidos.dto;

import com.empresa.pedidos.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PATRON: DTO + Static Factory Method
 * Rol: contrato de SALIDA. La entidad JPA nunca cruza la frontera HTTP.
 *
 * Beneficio adicional: al construir el DTO dentro de la transaccion se fuerza
 * la carga de las relaciones lazy, evitando LazyInitializationException al
 * serializar, y se elimina el riesgo de bucles infinitos de Jackson.
 */
public record PedidoResponse(
        Long id,
        String cliente,
        String estado,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        List<LineaResponse> lineas
) {

    /** Factory method: centraliza el mapeo en un unico lugar. */
    public static PedidoResponse de(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getNombre(),
                pedido.getEstado().name(),
                pedido.calcularSubtotal(),
                pedido.getDescuento(),
                pedido.getTotal(),
                pedido.getFechaCreacion(),
                pedido.getLineas().stream().map(l -> new LineaResponse(
                        l.getProducto().getNombre(),
                        l.getCantidad(),
                        l.getProducto().getPrecio(),
                        l.importe()
                )).toList()
        );
    }

    public record LineaResponse(
            String producto,
            int cantidad,
            BigDecimal precioUnitario,
            BigDecimal importe
    ) {
    }
}
