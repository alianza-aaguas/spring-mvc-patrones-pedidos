package com.empresa.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada.
 *
 * PATRON: Front Controller (aportado por Spring)
 * Rol: al arrancar, Spring registra el DispatcherServlet, unica puerta de entrada
 * para TODAS las peticiones HTTP. El resuelve el handler, invoca el @Controller
 * y convierte el resultado con los HttpMessageConverter.
 *
 * PATRON: Inversion de Control / Contenedor de beans
 * Rol: @SpringBootApplication incluye @ComponentScan, que descubre todos los
 * @Component/@Service/@Repository de este paquete hacia abajo y los registra.
 */
@SpringBootApplication
public class PedidosApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidosApplication.class, args);
    }
}
