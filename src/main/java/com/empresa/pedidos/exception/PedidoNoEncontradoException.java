package com.empresa.pedidos.exception;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(Long id) {
        super("Pedido no encontrado: " + id);
    }
}
