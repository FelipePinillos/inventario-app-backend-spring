package com.inventario.repository;

import com.inventario.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByEstado(String estado);
    List<Producto> findByCategoriaIdAndEstado(Long categoriaId, String estado);
    List<Producto> findByMarcaIdAndEstado(Long marcaId, String estado);
}
