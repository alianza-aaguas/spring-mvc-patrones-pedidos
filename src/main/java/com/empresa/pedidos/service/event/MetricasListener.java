package com.empresa.pedidos.service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * PATRON: Observer (listener)
 * Rol: segundo suscriptor del mismo evento, para demostrar el fan-out.
 *
 * Anadir suscriptores no modifica al publicador. Si uno falla, los demas
 * siguen ejecutandose (los listeners son independientes entre si).
 */
@Component
public class MetricasListener {

    private static final Logger log = LoggerFactory.getLogger(MetricasListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PedidoConfirmado evento) {
        log.info("[METRICAS] pedidos.confirmados +1 (importe={})", evento.total());
    }
}
