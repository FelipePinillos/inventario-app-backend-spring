package com.inventario.service;

import com.inventario.dto.PresentacionDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.Presentacion;
import com.inventario.models.Producto;
import com.inventario.repository.PresentacionRepository;
import com.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresentacionService {

    private final PresentacionRepository presentacionRepository;
    private final ProductoRepository productoRepository;

    public List<PresentacionDTO> findAll(boolean incluirInactivos) {
        List<Presentacion> presentaciones = incluirInactivos
                ? presentacionRepository.findAll()
                : presentacionRepository.findByEstado("A");
        return presentaciones.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PresentacionDTO findById(Long id) {
        Presentacion presentacion = presentacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentacion", id));
        return toDTO(presentacion);
    }

    @Transactional
    public PresentacionDTO create(PresentacionDTO.Create dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", dto.getProductoId()));

        Presentacion presentacion = Presentacion.builder()
                .nombre(dto.getNombre())
                .producto(producto)
                .cantidadBase(dto.getCantidadBase())
                .precioVenta(dto.getPrecioVenta())
                .precioCompra(dto.getPrecioCompra() != null ? dto.getPrecioCompra() : BigDecimal.ZERO)
                .estado("A")
                .build();

        return toDTO(presentacionRepository.save(presentacion));
    }

    @Transactional
    public PresentacionDTO update(Long id, PresentacionDTO.Update dto) {
        Presentacion presentacion = presentacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentacion", id));

        if (dto.getNombre() != null) presentacion.setNombre(dto.getNombre());
        if (dto.getCantidadBase() != null) presentacion.setCantidadBase(dto.getCantidadBase());
        if (dto.getPrecioVenta() != null) presentacion.setPrecioVenta(dto.getPrecioVenta());
        if (dto.getPrecioCompra() != null) presentacion.setPrecioCompra(dto.getPrecioCompra());
        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", dto.getProductoId()));
            presentacion.setProducto(producto);
        }

        return toDTO(presentacionRepository.save(presentacion));
    }

    @Transactional
    public void delete(Long id) {
        Presentacion presentacion = presentacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentacion", id));
        presentacion.setEstado("I");
        presentacionRepository.save(presentacion);
    }

    private PresentacionDTO toDTO(Presentacion presentacion) {
        return PresentacionDTO.builder()
                .id(presentacion.getId())
                .nombre(presentacion.getNombre())
                .productoId(presentacion.getProducto() != null ? presentacion.getProducto().getId() : null)
                .productoNombre(presentacion.getProducto() != null ? presentacion.getProducto().getNombre() : null)
                .cantidadBase(presentacion.getCantidadBase())
                .precioVenta(presentacion.getPrecioVenta())
                .precioCompra(presentacion.getPrecioCompra())
                .estado(presentacion.getEstado())
                .fechaCreacion(presentacion.getFechaCreacion())
                .fechaEdicion(presentacion.getFechaEdicion())
                .build();
    }
}
