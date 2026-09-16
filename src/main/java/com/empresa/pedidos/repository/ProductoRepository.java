package com.empresa.pedidos.repository;

import com.empresa.pedidos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PATRON: Repository
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
