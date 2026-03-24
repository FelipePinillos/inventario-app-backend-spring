package com.inventario.repository;

import com.inventario.models.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {
    List<TipoUsuario> findByEstado(String estado);
}
