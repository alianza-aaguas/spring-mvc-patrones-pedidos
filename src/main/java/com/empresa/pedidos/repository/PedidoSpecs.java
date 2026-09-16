package com.empresa.pedidos.repository;

import com.empresa.pedidos.model.Estado;
import com.empresa.pedidos.model.Pedido;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PATRON: Specification
 * Rol: cada metodo devuelve un predicado reutilizable y COMPONIBLE con and()/or().
 *
 * Por que: evita la explosion combinatoria de query methods
 * (findByEstado, findByEstadoAndCliente, findByEstadoAndClienteAndTotal...).
 * Con filtros opcionales, cada uno se anade solo si viene informado.
 *
 * Cada metodo devuelve null cuando el filtro no aplica: Specification.and()
 * ignora los null, asi que el codigo cliente no necesita ifs anidados.
 */
public final class PedidoSpecs {

    private PedidoSpecs() { }

    public static Specification<Pedido> porEstado(Estado estado) {
        return estado == null ? null
                : (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    public static Specification<Pedido> deCliente(Long clienteId) {
        return clienteId == null ? null
                : (root, query, cb) -> cb.equal(root.get("cliente").get("id"), clienteId);
    }

    public static Specification<Pedido> totalMayorQue(BigDecimal minimo) {
        return minimo == null ? null
                : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("total"), minimo);
    }

    public static Specification<Pedido> totalMenorQue(BigDecimal maximo) {
        return maximo == null ? null
                : (root, query, cb) -> cb.lessThanOrEqualTo(root.get("total"), maximo);
    }

    public static Specification<Pedido> entreFechas(LocalDateTime desde, LocalDateTime hasta) {
        if (desde == null && hasta == null) {
            return null;
        }
        return (root, query, cb) -> {
            if (desde == null) {
                return cb.lessThanOrEqualTo(root.get("fechaCreacion"), hasta);
            }
            if (hasta == null) {
                return cb.greaterThanOrEqualTo(root.get("fechaCreacion"), desde);
            }
            return cb.between(root.get("fechaCreacion"), desde, hasta);
        };
    }
}
