package com.empresa.pedidos.service.descuento;

import com.empresa.pedidos.model.Pedido;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PATRON: Strategy (estrategia concreta)
 * Rol: 10% si el pedido trae el cupon PROMO10.
 *
 * @Order(1): las estrategias se acumulan en orden determinista. Sin @Order el
 * orden dependeria del escaneo del classpath y podria variar entre entornos.
 */
@Component
@Order(1)
public class DescuentoCupon implements EstrategiaDescuento {

    private static final String CUPON_VALIDO = "PROMO10";
    private static final BigDecimal PORCENTAJE = new BigDecimal("0.10");

    @Override
    public boolean aplica(Pedido pedido) {
        return CUPON_VALIDO.equalsIgnoreCase(pedido.getCupon());
    }

    @Override
    public BigDecimal calcular(Pedido pedido) {
        return pedido.calcularSubtotal().multiply(PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String nombre() {
        return "CUPON_" + CUPON_VALIDO;
    }
}
