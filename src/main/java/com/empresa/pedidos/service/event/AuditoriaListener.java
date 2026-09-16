package com.empresa.pedidos.service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * PATRON: Observer (listener)
 * Rol: auditoria posterior a la confirmacion.
 *
 * AFTER_COMMIT es clave: si la transaccion hace rollback, este metodo NO se
 * ejecuta. Es la forma de evitar efectos externos (emails, webhooks) de
 * operaciones que luego se revierten: un email enviado no tiene rollback.
 *
 * Ademas, el publicador (PedidoServiceImpl) no conoce a este listener: el
 * acoplamiento es cero en ambos sentidos.
 */
@Component
public class AuditoriaListener {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PedidoConfirmado evento) {
        log.info("[AUDITORIA] pedido={} cliente={} total={}",
                evento.pedidoId(), evento.clienteId(), evento.total());
    }
}
