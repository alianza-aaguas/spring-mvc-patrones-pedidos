package com.empresa.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * PATRON: DTO (Data Transfer Object)
 * Rol: contrato de ENTRADA de la API.
 *
 * Por que un record y no la entidad Pedido:
 *  - impide que un cliente malicioso envie campos como estado o total
 *    (vulnerabilidad de mass assignment).
 *  - la API puede evolucionar sin arrastrar el esquema de base de datos.
 *  - las validaciones de formato viven aqui; las de negocio, en la entidad.
 */
public record PedidoRequest(

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotEmpty(message = "El pedido debe tener al menos una linea")
        @Valid
        List<LineaRequest> lineas,

        String cupon,

        /** Clave que resolvera la PasarelaFactory: stripe o paypal. */
        String pasarela
) {
}
