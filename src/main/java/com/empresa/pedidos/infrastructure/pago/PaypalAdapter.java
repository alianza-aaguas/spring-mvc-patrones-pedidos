package com.empresa.pedidos.infrastructure.pago;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * SDK externo SIMULADO con una firma COMPLETAMENTE distinta a la de Stripe:
 * recibe un Map y devuelve un Map. Es justo el motivo de existir del Adapter.
 */
@Component
class PaypalApiSimulada {

    private static final Logger log = LoggerFactory.getLogger(PaypalApiSimulada.class);

    Map<String, Object> executePayment(Map<String, Object> payload) {
        log.info("[paypal-sdk] executePayment({})", payload);
        return Map.of("status", "COMPLETED", "token", UUID.randomUUID().toString());
    }
}

/**
 * PATRON: Adapter (adaptador concreto)
 * Rol: misma interfaz de dominio, proveedor radicalmente distinto.
 *
 * El servicio invoca cobrar(...) sin enterarse de si detras hay un Map, un
 * SDK con centimos o una llamada REST.
 */
@Component("paypal")
public class PaypalAdapter implements PasarelaPago {

    private final PaypalApiSimulada sdk;

    PaypalAdapter(PaypalApiSimulada sdk) {
        this.sdk = sdk;
    }

    @Override
    public ResultadoPago cobrar(Long pedidoId, BigDecimal importe) {
        Map<String, Object> respuesta = sdk.executePayment(Map.of(
                "amount", importe.toPlainString(),
                "currency", "EUR",
                "invoice", "pedido-" + pedidoId));

        return "COMPLETED".equals(respuesta.get("status"))
                ? ResultadoPago.ok(String.valueOf(respuesta.get("token")))
                : ResultadoPago.rechazado(String.valueOf(respuesta.get("status")));
    }

    @Override
    public String nombre() {
        return "paypal";
    }
}
