package com.inventario.repository;

import com.inventario.models.Presentacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresentacionRepository extends JpaRepository<Presentacion, Long> {
    List<Presentacion> findByEstado(String estado);
    List<Presentacion> findByProductoIdAndEstado(Long productoId, String estado);
}
