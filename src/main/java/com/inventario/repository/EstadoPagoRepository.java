package com.inventario.repository;

import com.inventario.models.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoPagoRepository extends JpaRepository<EstadoPago, Long> {
    List<EstadoPago> findByEstado(String estado);
}
