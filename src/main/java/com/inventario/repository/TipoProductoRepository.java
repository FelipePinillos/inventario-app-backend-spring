package com.inventario.repository;

import com.inventario.models.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoProductoRepository extends JpaRepository<TipoProducto, Long> {
    List<TipoProducto> findByEstado(String estado);
}
