package com.inventario.service;

import com.inventario.dto.CompraDTO;
import com.inventario.dto.DetalleCompraDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.*;
import com.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final EstadoPagoRepository estadoPagoRepository;

    public List<CompraDTO> findAll(boolean incluirInactivos) {
        List<Compra> compras = incluirInactivos
                ? compraRepository.findAll()
                : compraRepository.findByEstado("A");
        return compras.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CompraDTO findById(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));
        return toDTO(compra);
    }

    @Transactional
    public CompraDTO create(CompraDTO.Create dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", dto.getProveedorId()));
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioId()));
        EstadoPago estadoPago = estadoPagoRepository.findById(dto.getEstadoPagoId())
                .orElseThrow(() -> new ResourceNotFoundException("EstadoPago", dto.getEstadoPagoId()));

        BigDecimal total = calcularTotal(dto.getDetalles());

        Compra compra = Compra.builder()
                .proveedor(proveedor)
                .usuario(usuario)
                .estadoPago(estadoPago)
                .fecha(dto.getFecha())
                .total(total)
                .observaciones(dto.getObservaciones())
                .estado("A")
                .build();

        Compra savedCompra = compraRepository.save(compra);

        List<DetalleCompra> detalles = dto.getDetalles().stream().map(d -> {
            Producto producto = productoRepository.findById(d.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", d.getProductoId()));

            producto.setStock(producto.getStock() + d.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
            return DetalleCompra.builder()
                    .compra(savedCompra)
                    .producto(producto)
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .subtotal(subtotal)
                    .estado("A")
                    .build();
        }).collect(Collectors.toList());

        detalleCompraRepository.saveAll(detalles);
        savedCompra.setDetalles(detalles);

        return toDTO(savedCompra);
    }

    @Transactional
    public CompraDTO update(Long id, CompraDTO.Update dto) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));

        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", dto.getProveedorId()));
            compra.setProveedor(proveedor);
        }
        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioId()));
            compra.setUsuario(usuario);
        }
        if (dto.getEstadoPagoId() != null) {
            EstadoPago estadoPago = estadoPagoRepository.findById(dto.getEstadoPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException("EstadoPago", dto.getEstadoPagoId()));
            compra.setEstadoPago(estadoPago);
        }
        if (dto.getFecha() != null) compra.setFecha(dto.getFecha());
        if (dto.getObservaciones() != null) compra.setObservaciones(dto.getObservaciones());

        return toDTO(compraRepository.save(compra));
    }

    @Transactional
    public void delete(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));
        compra.setEstado("I");
        compraRepository.save(compra);
    }

    private BigDecimal calcularTotal(List<DetalleCompraDTO.Create> detalles) {
        return detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CompraDTO toDTO(Compra compra) {
        List<DetalleCompraDTO> detallesDTO = compra.getDetalles() != null
                ? compra.getDetalles().stream().map(this::detalleToDTO).collect(Collectors.toList())
                : List.of();

        return CompraDTO.builder()
                .id(compra.getId())
                .proveedorId(compra.getProveedor() != null ? compra.getProveedor().getId() : null)
                .proveedorNombre(compra.getProveedor() != null ? compra.getProveedor().getNombre() : null)
                .usuarioId(compra.getUsuario() != null ? compra.getUsuario().getId() : null)
                .usuarioNombre(compra.getUsuario() != null
                        ? compra.getUsuario().getNombre() + " " + compra.getUsuario().getApellido() : null)
                .estadoPagoId(compra.getEstadoPago() != null ? compra.getEstadoPago().getId() : null)
                .estadoPagoNombre(compra.getEstadoPago() != null ? compra.getEstadoPago().getNombre() : null)
                .fecha(compra.getFecha())
                .total(compra.getTotal())
                .observaciones(compra.getObservaciones())
                .estado(compra.getEstado())
                .detalles(detallesDTO)
                .createdAt(compra.getCreatedAt())
                .updatedAt(compra.getUpdatedAt())
                .build();
    }

    private DetalleCompraDTO detalleToDTO(DetalleCompra detalle) {
        return DetalleCompraDTO.builder()
                .id(detalle.getId())
                .compraId(detalle.getCompra() != null ? detalle.getCompra().getId() : null)
                .productoId(detalle.getProducto() != null ? detalle.getProducto().getId() : null)
                .productoNombre(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .estado(detalle.getEstado())
                .createdAt(detalle.getCreatedAt())
                .updatedAt(detalle.getUpdatedAt())
                .build();
    }
}
