package com.empresa.pedidos.service.handler;

import com.empresa.pedidos.infrastructure.MailService;
import com.empresa.pedidos.model.Pedido;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * PATRON: Handler concreto (Plugin)
 * Rol: envia email solo si el cliente tiene direccion.
 * La condicion vive en soporta(), no en un if del servicio.
 */
@Component
@Order(2)
public class EnviarEmailHandler implements PedidoHandler {

    private final MailService mail;

    public EnviarEmailHandler(MailService mail) {
        this.mail = mail;
    }

    @Override
    public boolean soporta(Pedido pedido) {
        return pedido.getCliente().tieneEmail();
    }

    @Override
    public void manejar(Pedido pedido) {
        mail.enviarConfirmacion(pedido);
    }
}
