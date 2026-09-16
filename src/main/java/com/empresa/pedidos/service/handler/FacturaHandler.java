package com.empresa.pedidos.service.handler;

import com.empresa.pedidos.infrastructure.FacturaService;
import com.empresa.pedidos.model.Pedido;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * PATRON: Handler concreto (Plugin)
 * Rol: genera factura solo a partir de 300 EUR.
 */
@Component
@Order(3)
public class FacturaHandler implements PedidoHandler {

    private static final BigDecimal UMBRAL = new BigDecimal("300");

    private final FacturaService facturas;

    public FacturaHandler(FacturaService facturas) {
        this.facturas = facturas;
    }

    @Override
    public boolean soporta(Pedido pedido) {
        return pedido.getTotal().compareTo(UMBRAL) >= 0;
    }

    @Override
    public void manejar(Pedido pedido) {
        facturas.generar(pedido);
    }
}
