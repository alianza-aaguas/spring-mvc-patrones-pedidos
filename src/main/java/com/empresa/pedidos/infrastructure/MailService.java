package com.empresa.pedidos.infrastructure;

import com.empresa.pedidos.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    public void enviarConfirmacion(Pedido pedido) {
        log.info("[MAIL] a {} -> pedido {} confirmado por {} EUR",
                pedido.getCliente().getEmail(), pedido.getId(), pedido.getTotal());
    }
}
