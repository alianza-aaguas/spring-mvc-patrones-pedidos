package com.empresa.pedidos.controller;

import com.empresa.pedidos.dto.PedidoRequest;
import com.empresa.pedidos.dto.PedidoResponse;
import com.empresa.pedidos.model.Estado;
import com.empresa.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * PATRON: Layered Architecture (capa de presentacion)
 * Rol: traducir HTTP <-> casos de uso. NADA MAS.
 *
 * Reglas que se respetan aqui:
 *  - cero logica de negocio (anti "controlador gordo").
 *  - depende de la INTERFAZ PedidoService, no de la implementacion.
 *  - nunca devuelve entidades JPA, solo DTOs.
 *  - no captura excepciones: de eso se encarga el @RestControllerAdvice.
 *
 * PATRON: Front Controller
 * Este metodo no se invoca directamente: el DispatcherServlet recibe la
 * peticion, la enruta hasta aqui y serializa el retorno con Jackson.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    /** PATRON: Inyeccion por constructor contra la abstraccion. */
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse creado = pedidoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/{id}/confirmar")
    public PedidoResponse confirmar(@PathVariable Long id) {
        return pedidoService.confirmar(id);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        return pedidoService.obtener(id);
    }

    /** Filtros OPCIONALES resueltos con Specifications en la capa de servicio. */
    @GetMapping
    public List<PedidoResponse> buscar(
            @RequestParam(required = false) Estado estado,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) BigDecimal totalMinimo) {
        return pedidoService.buscar(estado, clienteId, totalMinimo);
    }

    /** PATRON: Template Method (ExportadorCsv). */
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<String> exportar() {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/csv"))
                .body(pedidoService.exportarCsv());
    }
}
