package com.empresa.pedidos.service.handler;

import com.empresa.pedidos.infrastructure.SmsService;
import com.empresa.pedidos.model.Pedido;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * PATRON: Handler concreto (Plugin)
 * Rol: demuestra la extensibilidad. Esta clase se anadio DESPUES y no obligo a
 * tocar PedidoServiceImpl ni ninguna clase de configuracion.
 */
@Component
@Order(4)
public class NotificarSmsHandler implements PedidoHandler {

    private final SmsService sms;

    public NotificarSmsHandler(SmsService sms) {
        this.sms = sms;
    }

    @Override
    public boolean soporta(Pedido pedido) {
        return pedido.getCliente().tieneMovil();
    }

    @Override
    public void manejar(Pedido pedido) {
        sms.enviar(pedido.getCliente().getMovil(), "Pedido " + pedido.getId() + " confirmado");
    }
}
