package com.inventario.repository;

import com.inventario.models.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByEstado(String estado);
    List<Compra> findByProveedorIdAndEstado(Long proveedorId, String estado);
}
