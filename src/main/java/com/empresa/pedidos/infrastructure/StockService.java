package com.empresa.pedidos.infrastructure;

import com.empresa.pedidos.model.LineaPedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PATRON: Singleton de Spring (bean stateless)
 * Rol: servicio simulado. Sin estado mutable, por lo que una unica instancia
 * puede ser compartida con seguridad por todos los hilos.
 */
@Service
public class StockService {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    public void descontar(List<LineaPedido> lineas) {
        lineas.forEach(l -> log.info("[STOCK] -{} uds de '{}'",
                l.getCantidad(), l.getProducto().getNombre()));
    }
}
