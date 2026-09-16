package com.empresa.pedidos.service.descuento;

import com.empresa.pedidos.model.Pedido;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PATRON: Strategy (estrategia concreta)
 * Rol: 5% a partir de 5 unidades en el pedido.
 */
@Component
@Order(2)
public class DescuentoVolumen implements EstrategiaDescuento {

    private static final int UNIDADES_MINIMAS = 5;
    private static final BigDecimal PORCENTAJE = new BigDecimal("0.05");

    @Override
    public boolean aplica(Pedido pedido) {
        return pedido.unidadesTotales() >= UNIDADES_MINIMAS;
    }

    @Override
    public BigDecimal calcular(Pedido pedido) {
        return pedido.calcularSubtotal().multiply(PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String nombre() {
        return "VOLUMEN";
    }
}
