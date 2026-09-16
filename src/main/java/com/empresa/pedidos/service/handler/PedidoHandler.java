package com.empresa.pedidos.service.handler;

import com.empresa.pedidos.model.Pedido;

/**
 * PATRON: Strategy / Handler registry (Plugin Pattern)
 * Rol: contrato de una accion posterior a la confirmacion de un pedido.
 *
 * Por que: el servicio inyecta List<PedidoHandler> y NO conoce a ninguna
 * implementacion concreta. Anadir una accion nueva = crear una clase con
 * @Component. Cero cambios en el servicio.
 *
 * Nota de diseno: se define una interfaz propia en lugar de usar
 * Consumer<Pedido> porque el tipo comunica intencion de dominio y admite
 * mas de un metodo (aqui, soporta()).
 */
public interface PedidoHandler {

    /** El handler decide si le toca. Sustituye a un if/switch externo. */
    boolean soporta(Pedido pedido);

    void manejar(Pedido pedido);
}
