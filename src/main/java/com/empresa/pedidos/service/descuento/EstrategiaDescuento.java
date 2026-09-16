package com.empresa.pedidos.service.descuento;

import com.empresa.pedidos.model.Pedido;

import java.math.BigDecimal;

/**
 * PATRON: Strategy (self-selecting)
 * Rol: interfaz de la estrategia.
 *
 * Por que: sin esto, el servicio tendria un switch sobre el tipo de descuento
 * que crece con cada promocion nueva. Aqui cada estrategia decide si le toca
 * mediante aplica(), y anadir una promocion es crear una clase, no modificar
 * el servicio (principio abierto/cerrado).
 */
public interface EstrategiaDescuento {

    /** El propio descuento decide si es aplicable a este pedido. */
    boolean aplica(Pedido pedido);

    /** Importe a descontar. Nunca negativo. */
    BigDecimal calcular(Pedido pedido);

    /** Nombre legible, util para trazas y para la respuesta al cliente. */
    String nombre();
}
