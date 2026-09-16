package com.empresa.pedidos.infrastructure;

import com.empresa.pedidos.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FacturaService {

    private static final Logger log = LoggerFactory.getLogger(FacturaService.class);

    public void generar(Pedido pedido) {
        log.info("[FACTURA] generada para pedido {} por {} EUR", pedido.getId(), pedido.getTotal());
    }
}
