package com.empresa.pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * PATRON: DTO
 * Rol: linea de entrada. Validacion de FORMATO con Bean Validation;
 * la validacion de NEGOCIO permanece en el modelo de dominio.
 */
public record LineaRequest(

        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @Min(value = 1, message = "La cantidad minima es 1")
        int cantidad
) {
}
