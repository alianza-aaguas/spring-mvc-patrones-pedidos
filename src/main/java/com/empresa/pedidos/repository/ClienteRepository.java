package com.empresa.pedidos.repository;

import com.empresa.pedidos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PATRON: Repository
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
