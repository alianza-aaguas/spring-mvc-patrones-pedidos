package com.empresa.pedidos.infrastructure.pago;

import java.math.BigDecimal;

/**
 * PATRON: Adapter (interfaz destino / Target)
 * Rol: contrato PROPIO de pago, expresado en el lenguaje de nuestro dominio.
 *
 * Por que: ninguna clase del dominio conoce a Stripe ni a PayPal. Si manana
 * cambiamos de proveedor, se escribe un adaptador nuevo y no se toca el
 * servicio. Tambien hace trivial el test: un doble de esta interfaz.
 */
public interface PasarelaPago {

    ResultadoPago cobrar(Long pedidoId, BigDecimal importe);

    /** Clave con la que la Factory lo localiza en runtime. */
    String nombre();
}
