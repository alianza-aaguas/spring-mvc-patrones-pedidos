package com.empresa.pedidos.infrastructure.pago;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * PATRON: Factory
 * Rol: selecciona la pasarela en RUNTIME a partir de una clave.
 *
 * Truco de Spring: al inyectar Map<String, PasarelaPago>, el contenedor rellena
 * el mapa con NOMBRE-DE-BEAN -> instancia. Por eso los adaptadores se declaran
 * como @Component("stripe") y @Component("paypal").
 *
 * Resultado: cero switch. Anadir una pasarela nueva = una clase mas; esta
 * factory no se modifica jamas.
 */
@Component
public class PasarelaFactory {

    private static final String POR_DEFECTO = "stripe";

    private final Map<String, PasarelaPago> pasarelas;

    public PasarelaFactory(Map<String, PasarelaPago> pasarelas) {
        this.pasarelas = pasarelas;
    }

    public PasarelaPago obtener(String clave) {
        String buscada = (clave == null || clave.isBlank()) ? POR_DEFECTO : clave.toLowerCase();
        PasarelaPago pasarela = pasarelas.get(buscada);
        if (pasarela == null) {
            throw new IllegalArgumentException(
                    "Pasarela no soportada: " + clave + ". Disponibles: " + disponibles());
        }
        return pasarela;
    }

    public Set<String> disponibles() {
        return pasarelas.keySet();
    }
}
