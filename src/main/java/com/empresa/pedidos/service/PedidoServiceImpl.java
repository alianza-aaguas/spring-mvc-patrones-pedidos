package com.empresa.pedidos.service;

import com.empresa.pedidos.dto.PedidoRequest;
import com.empresa.pedidos.dto.PedidoResponse;
import com.empresa.pedidos.exception.PagoRechazadoException;
import com.empresa.pedidos.exception.PedidoNoEncontradoException;
import com.empresa.pedidos.exception.RecursoNoEncontradoException;
import com.empresa.pedidos.infrastructure.ExportadorCsv;
import com.empresa.pedidos.infrastructure.pago.PasarelaFactory;
import com.empresa.pedidos.infrastructure.pago.PasarelaPago;
import com.empresa.pedidos.infrastructure.pago.ResultadoPago;
import com.empresa.pedidos.model.Cliente;
import com.empresa.pedidos.model.Estado;
import com.empresa.pedidos.model.LineaPedido;
import com.empresa.pedidos.model.Pedido;
import com.empresa.pedidos.model.Producto;
import com.empresa.pedidos.repository.ClienteRepository;
import com.empresa.pedidos.repository.PedidoRepository;
import com.empresa.pedidos.repository.PedidoSpecs;
import com.empresa.pedidos.repository.ProductoRepository;
import com.empresa.pedidos.service.descuento.EstrategiaDescuento;
import com.empresa.pedidos.service.event.PedidoConfirmado;
import com.empresa.pedidos.service.handler.PedidoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.empresa.pedidos.infrastructure.ExportadorCsv.escribirLinea;

/**
 * PATRON: Facade
 * Rol: unico punto de entrada del caso de uso. Detras coordina repositorios,
 * estrategias, pasarela de pago, handlers y eventos. El controlador invoca UN
 * metodo y desconoce por completo esa orquestacion.
 *
 * PATRON: Proxy (via @Transactional)
 * Rol: Spring NO inyecta esta clase, sino un proxy que la envuelve y abre o
 * cierra la transaccion alrededor de cada metodo publico anotado.
 *
 * TRAMPA CLASICA (autoinvocacion): si dentro de crear() llamaramos a
 * this.confirmar(), esa llamada NO pasaria por el proxy y su @Transactional
 * seria ignorado, porque el proxy solo intercepta llamadas que entran DESDE
 * FUERA del bean. Misma razon por la que @Transactional en metodos privados
 * no tiene ningun efecto.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final ProductoRepository productos;

    /**
     * PATRON: Strategy + Plugin registry
     * Spring inyecta TODAS las implementaciones registradas, ya ordenadas por
     * @Order. Este servicio no menciona ni una sola clase concreta, por lo que
     * anadir una promocion o una accion nueva no obliga a tocar este archivo.
     */
    private final List<EstrategiaDescuento> estrategias;
    private final List<PedidoHandler> handlers;

    private final PasarelaFactory pasarelas;
    private final ExportadorCsv exportador;

    /** PATRON: Inyeccion por constructor. Campos final, sin @Autowired. */
    public PedidoServiceImpl(PedidoRepository pedidos,
                             ClienteRepository clientes,
                             ProductoRepository productos,
                             List<EstrategiaDescuento> estrategias,
                             List<PedidoHandler> handlers,
                             PasarelaFactory pasarelas,
                             ExportadorCsv exportador,
                             ApplicationEventPublisher publisher) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.productos = productos;
        this.estrategias = estrategias;
        this.handlers = handlers;
        this.pasarelas = pasarelas;
        this.exportador = exportador;
        this.publisher = publisher;
    }

    /** PATRON: Observer (publicador). No conoce a ninguno de sus suscriptores. */
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public PedidoResponse crear(PedidoRequest request) {
        Cliente cliente = clientes.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado: " + request.clienteId()));

        // PATRON: Builder
        Pedido.Builder builder = Pedido.builder().cliente(cliente).cupon(request.cupon());

        request.lineas().forEach(l -> {
            Producto producto = productos.findById(l.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado: " + l.productoId()));
            builder.linea(new LineaPedido(producto, l.cantidad()));
        });

        Pedido pedido = builder.build();

        aplicarDescuentos(pedido);

        return PedidoResponse.de(pedidos.save(pedido));
    }

    /**
     * PATRON: Strategy en accion.
     * Comparalo con la alternativa: un if/else if por cada promocion. Aqui el
     * bucle no cambia nunca, por muchas promociones que se anadan.
     */
    private void aplicarDescuentos(Pedido pedido) {
        estrategias.stream()
                .filter(e -> e.aplica(pedido))
                .forEach(e -> {
                    BigDecimal importe = e.calcular(pedido);
                    log.debug("Descuento {} -> -{}", e.nombre(), importe);
                    pedido.aplicarDescuento(importe);
                });
        pedido.recalcularTotal();
    }

    @Override
    @Transactional
    public PedidoResponse confirmar(Long pedidoId) {
        Pedido pedido = pedidos.findById(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));

        // La regla de negocio vive en la ENTIDAD (Rich Domain Model).
        pedido.confirmar();

        // PATRON: Factory -> pasarela elegida en runtime.
        PasarelaPago pasarela = pasarelas.obtener(null);
        ResultadoPago resultado = pasarela.cobrar(pedido.getId(), pedido.getTotal());
        if (!resultado.exito()) {
            // Excepcion no chequeada -> rollback automatico de la transaccion.
            throw new PagoRechazadoException(resultado.motivoRechazo());
        }

        pedidos.save(pedido);

        ejecutarHandlers(pedido);

        // PATRON: Observer. Los listeners son AFTER_COMMIT: si esta transaccion
        // termina en rollback, no se ejecutan. Un email enviado no tiene rollback.
        publisher.publishEvent(new PedidoConfirmado(
                pedido.getId(), pedido.getCliente().getId(), pedido.getTotal()));

        return PedidoResponse.de(pedido);
    }

    /**
     * PATRON: Plugin registry con AISLAMIENTO DE FALLOS.
     *
     * Cada handler se ejecuta en su propio try/catch: que falle el SMS no puede
     * tumbar una confirmacion de pedido ya cobrada. Se registra el error y se
     * continua con el siguiente handler.
     */
    private void ejecutarHandlers(Pedido pedido) {
        handlers.stream()
                .filter(h -> h.soporta(pedido))
                .forEach(h -> {
                    try {
                        h.manejar(pedido);
                    } catch (RuntimeException e) {
                        log.error("Handler {} fallo para el pedido {}: {}",
                                h.getClass().getSimpleName(), pedido.getId(), e.getMessage());
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long pedidoId) {
        return pedidos.findById(pedidoId)
                .map(PedidoResponse::de)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));
    }

    /**
     * PATRON: Specification.
     * Filtros opcionales compuestos con and(), sin un solo if y sin necesidad
     * de un query method distinto por cada combinacion.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> buscar(Estado estado, Long clienteId, BigDecimal totalMinimo) {
        Specification<Pedido> filtro = Specification
                .where(PedidoSpecs.porEstado(estado))
                .and(PedidoSpecs.deCliente(clienteId))
                .and(PedidoSpecs.totalMayorQue(totalMinimo));

        return pedidos.findAll(filtro).stream().map(PedidoResponse::de).toList();
    }

    /** PATRON: Template Method. Solo aportamos las filas; el resto lo pone el exportador. */
    @Override
    @Transactional(readOnly = true)
    public String exportarCsv() {
        List<Pedido> todos = pedidos.findAll();
        return exportador.exportar("id;cliente;estado;total", writer ->
                todos.forEach(p -> escribirLinea(writer, String.join(";",
                        String.valueOf(p.getId()),
                        p.getCliente().getNombre(),
                        p.getEstado().name(),
                        p.getTotal().toPlainString()))));
    }
}
