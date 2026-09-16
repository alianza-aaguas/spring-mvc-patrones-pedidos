package com.empresa.pedidos.service;

import com.empresa.pedidos.dto.PedidoRequest;
import com.empresa.pedidos.dto.PedidoResponse;
import com.empresa.pedidos.model.Estado;

import java.math.BigDecimal;
import java.util.List;

/**
 * PATRON: Interface Segregation / Programar contra abstracciones
 * Rol: contrato del caso de uso.
 *
 * Por que existe la interfaz: permite sustituir la implementacion en tests y
 * es lo que Spring necesita para envolver el bean en un proxy JDK dinamico
 * cuando aplica @Transactional.
 */
public interface PedidoService {

    PedidoResponse crear(PedidoRequest request);

    PedidoResponse confirmar(Long pedidoId);

    PedidoResponse obtener(Long pedidoId);

    List<PedidoResponse> buscar(Estado estado, Long clienteId, BigDecimal totalMinimo);

    String exportarCsv();
}
