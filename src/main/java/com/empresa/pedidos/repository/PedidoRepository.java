package com.empresa.pedidos.repository;

import com.empresa.pedidos.model.Cliente;
import com.empresa.pedidos.model.Estado;
import com.empresa.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PATRON: Repository
 * Rol: abstrae la persistencia tras una interfaz con vocabulario de dominio.
 * El servicio no sabe si detras hay JPA, JDBC o un mapa en memoria.
 *
 * Detalle: es una INTERFAZ sin implementacion. Spring Data genera un proxy en
 * runtime y lo registra como bean. @Repository ademas traduce las excepciones
 * de persistencia a la jerarquia DataAccessException de Spring.
 *
 * JpaSpecificationExecutor habilita el patron Specification (ver PedidoSpecs).
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    /** Query method derivado: Spring deriva el SQL del nombre del metodo. */
    List<Pedido> findByClienteAndEstado(Cliente cliente, Estado estado);

    long countByEstado(Estado estado);
}
