package com.inventario.service;

import com.inventario.dto.DetalleVentaDTO;
import com.inventario.dto.VentaDTO;
import com.inventario.exception.BadRequestException;
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
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final EstadoPagoRepository estadoPagoRepository;

    public List<VentaDTO> findAll(boolean incluirInactivos) {
        List<Venta> ventas = incluirInactivos
                ? ventaRepository.findAll()
                : ventaRepository.findByEstado("A");
        return ventas.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public VentaDTO findById(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));
        return toDTO(venta);
    }

    @Transactional
    public VentaDTO create(VentaDTO.Create dto) {
        Cliente cliente = null;
        if (dto.getClienteId() != null) {
            cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getClienteId()));
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioId()));
        EstadoPago estadoPago = estadoPagoRepository.findById(dto.getEstadoPagoId())
                .orElseThrow(() -> new ResourceNotFoundException("EstadoPago", dto.getEstadoPagoId()));

        BigDecimal total = calcularTotal(dto.getDetalles());

        Venta venta = Venta.builder()
                .cliente(cliente)
                .usuario(usuario)
                .estadoPago(estadoPago)
                .fecha(dto.getFecha())
                .total(total)
                .observaciones(dto.getObservaciones())
                .estado("A")
                .build();

        Venta savedVenta = ventaRepository.save(venta);

        List<DetalleVenta> detalles = dto.getDetalles().stream().map(d -> {
            Producto producto = productoRepository.findById(d.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", d.getProductoId()));

            if (producto.getStock() < d.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - d.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
            return DetalleVenta.builder()
                    .venta(savedVenta)
                    .producto(producto)
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .subtotal(subtotal)
                    .estado("A")
                    .build();
        }).collect(Collectors.toList());

        detalleVentaRepository.saveAll(detalles);
        savedVenta.setDetalles(detalles);

        return toDTO(savedVenta);
    }

    @Transactional
    public VentaDTO update(Long id, VentaDTO.Update dto) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getClienteId()));
            venta.setCliente(cliente);
        }
        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioId()));
            venta.setUsuario(usuario);
        }
        if (dto.getEstadoPagoId() != null) {
            EstadoPago estadoPago = estadoPagoRepository.findById(dto.getEstadoPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException("EstadoPago", dto.getEstadoPagoId()));
            venta.setEstadoPago(estadoPago);
        }
        if (dto.getFecha() != null) venta.setFecha(dto.getFecha());
        if (dto.getObservaciones() != null) venta.setObservaciones(dto.getObservaciones());

        return toDTO(ventaRepository.save(venta));
    }

    @Transactional
    public void delete(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));
        venta.setEstado("I");
        ventaRepository.save(venta);
    }

    private BigDecimal calcularTotal(List<DetalleVentaDTO.Create> detalles) {
        return detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private VentaDTO toDTO(Venta venta) {
        List<DetalleVentaDTO> detallesDTO = venta.getDetalles() != null
                ? venta.getDetalles().stream().map(this::detalleToDTO).collect(Collectors.toList())
                : List.of();

        return VentaDTO.builder()
                .id(venta.getId())
                .clienteId(venta.getCliente() != null ? venta.getCliente().getId() : null)
                .clienteNombre(venta.getCliente() != null
                        ? venta.getCliente().getNombre() + " " + venta.getCliente().getApellido() : null)
                .usuarioId(venta.getUsuario() != null ? venta.getUsuario().getId() : null)
                .usuarioNombre(venta.getUsuario() != null
                        ? venta.getUsuario().getNombre() + " " + venta.getUsuario().getApellido() : null)
                .estadoPagoId(venta.getEstadoPago() != null ? venta.getEstadoPago().getId() : null)
                .estadoPagoNombre(venta.getEstadoPago() != null ? venta.getEstadoPago().getNombre() : null)
                .fecha(venta.getFecha())
                .total(venta.getTotal())
                .observaciones(venta.getObservaciones())
                .estado(venta.getEstado())
                .detalles(detallesDTO)
                .createdAt(venta.getCreatedAt())
                .updatedAt(venta.getUpdatedAt())
                .build();
    }

    private DetalleVentaDTO detalleToDTO(DetalleVenta detalle) {
        return DetalleVentaDTO.builder()
                .id(detalle.getId())
                .ventaId(detalle.getVenta() != null ? detalle.getVenta().getId() : null)
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
