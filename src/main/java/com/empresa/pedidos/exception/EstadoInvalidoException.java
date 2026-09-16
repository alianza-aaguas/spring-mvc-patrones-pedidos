package com.empresa.pedidos.exception;

/** Excepcion de dominio: transicion de estado no permitida. */
public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
