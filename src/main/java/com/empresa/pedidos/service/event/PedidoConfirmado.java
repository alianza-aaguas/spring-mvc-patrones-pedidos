package com.empresa.pedidos.service.event;

import java.math.BigDecimal;

/**
 * PATRON: Observer / Publish-Subscribe
 * Rol: evento de dominio inmutable.
 *
 * Lleva solo datos primitivos (id, total), NO la entidad JPA: los listeners
 * AFTER_COMMIT se ejecutan fuera de la transaccion y una entidad lazy alli
 * provocaria LazyInitializationException.
 */
public record PedidoConfirmado(Long pedidoId, Long clienteId, BigDecimal total) {
}
