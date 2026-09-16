package com.empresa.pedidos.service.handler;

import com.empresa.pedidos.infrastructure.StockService;
import com.empresa.pedidos.model.Pedido;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * PATRON: Handler concreto (Plugin)
 * Rol: descuenta stock. Siempre aplica.
 */
@Component
@Order(1)
public class ActualizarStockHandler implements PedidoHandler {

    private final StockService stock;

    /** PATRON: Inyeccion por constructor (campo final, sin @Autowired en campos). */
    public ActualizarStockHandler(StockService stock) {
        this.stock = stock;
    }

    @Override
    public boolean soporta(Pedido pedido) {
        return true;
    }

    @Override
    public void manejar(Pedido pedido) {
        stock.descontar(pedido.getLineas());
    }
}
