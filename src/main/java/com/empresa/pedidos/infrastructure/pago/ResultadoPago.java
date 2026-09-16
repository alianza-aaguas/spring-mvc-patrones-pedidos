package com.empresa.pedidos.infrastructure.pago;

/** Resultado normalizado, independiente del proveedor. */
public record ResultadoPago(boolean exito, String referencia, String motivoRechazo) {

    public static ResultadoPago ok(String referencia) {
        return new ResultadoPago(true, referencia, null);
    }

    public static ResultadoPago rechazado(String motivo) {
        return new ResultadoPago(false, null, motivo);
    }
}
