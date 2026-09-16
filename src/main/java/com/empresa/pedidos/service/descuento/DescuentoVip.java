package com.empresa.pedidos.service.descuento;

import com.empresa.pedidos.model.Pedido;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PATRON: Strategy (estrategia concreta)
 * Rol: 8% para clientes VIP.
 */
@Component
@Order(3)
public class DescuentoVip implements EstrategiaDescuento {

    private static final BigDecimal PORCENTAJE = new BigDecimal("0.08");

    @Override
    public boolean aplica(Pedido pedido) {
        return pedido.getCliente().esVip();
    }

    @Override
    public BigDecimal calcular(Pedido pedido) {
        return pedido.calcularSubtotal().multiply(PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String nombre() {
        return "VIP";
    }
}
