package com.empresa.pedidos.infrastructure.pago;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * SDK externo SIMULADO. Representa codigo de terceros que no controlamos:
 * firma incomoda (centimos en long), tipos propios, nombres en ingles.
 */
@Component
class StripeApiSimulada {

    private static final Logger log = LoggerFactory.getLogger(StripeApiSimulada.class);

    /** Nota la firma ajena: importe en centimos y devuelve un String crudo. */
    String createCharge(long amountInCents, String currency, String idempotencyKey) {
        log.info("[stripe-sdk] createCharge({} cents, {}, {})", amountInCents, currency, idempotencyKey);
        return "ch_" + idempotencyKey;
    }
}

/**
 * PATRON: Adapter (adaptador concreto)
 * Rol: traduce nuestro contrato PasarelaPago a la API de Stripe.
 *
 * Aqui es donde vive la conversion EUR -> centimos. Esa fealdad queda
 * encapsulada en un unico punto en lugar de contaminar el servicio.
 */
@Component("stripe")
public class StripeAdapter implements PasarelaPago {

    private final StripeApiSimulada sdk;

    StripeAdapter(StripeApiSimulada sdk) {
        this.sdk = sdk;
    }

    @Override
    public ResultadoPago cobrar(Long pedidoId, BigDecimal importe) {
        long centimos = importe.movePointRight(2).longValueExact();
        String referencia = sdk.createCharge(centimos, "EUR", "pedido-" + pedidoId);
        return ResultadoPago.ok(referencia);
    }

    @Override
    public String nombre() {
        return "stripe";
    }
}
