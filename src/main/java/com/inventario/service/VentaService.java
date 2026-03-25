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
    private final PresentacionRepository presentacionRepository;

    public List<VentaDTO> findAll(boolean incluirInactivos) {
        List<Venta> ventas = incluirInactivos
                ? ventaRepository.findAll()
                : ventaRepository.findByEstado("CONFIRMADA");
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

        BigDecimal descuento = dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO;
        BigDecimal totalSinDescuento = calcularTotal(dto.getDetalles());
        BigDecimal totalConDescuento = totalSinDescuento.subtract(descuento);

        Venta venta = Venta.builder()
                .cliente(cliente)
                .usuario(usuario)
                .fecha(dto.getFecha())
                .descuento(descuento)
                .totalSinDescuento(totalSinDescuento)
                .totalConDescuento(totalConDescuento)
                .clienteNombre(dto.getClienteNombre())
                .clienteDni(dto.getClienteDni())
                .estado("CONFIRMADA")
                .build();

        Venta savedVenta = ventaRepository.save(venta);

        List<DetalleVenta> detalles = dto.getDetalles().stream().map(d -> {
            Presentacion presentacion = presentacionRepository.findById(d.getPresentacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Presentacion", d.getPresentacionId()));

            // Actualizar stock del producto
            Producto producto = presentacion.getProducto();
            int nuevoCantidad = d.getCantidad() * presentacion.getCantidadBase();
            if (producto.getStockActual() != null && producto.getStockActual() < nuevoCantidad) {
                throw new BadRequestException("Stock insuficiente para: " + producto.getNombre());
            }
            if (producto.getStockActual() != null) {
                producto.setStockActual(producto.getStockActual() - nuevoCantidad);
            }

            BigDecimal subtotal = d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
            return DetalleVenta.builder()
                    .venta(savedVenta)
                    .presentacion(presentacion)
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .subtotal(subtotal)
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
        if (dto.getFecha() != null) venta.setFecha(dto.getFecha());
        if (dto.getDescuento() != null) {
            venta.setDescuento(dto.getDescuento());
            if (venta.getTotalSinDescuento() != null) {
                venta.setTotalConDescuento(venta.getTotalSinDescuento().subtract(dto.getDescuento()));
            }
        }
        if (dto.getEstado() != null) venta.setEstado(dto.getEstado());
        if (dto.getClienteNombre() != null) venta.setClienteNombre(dto.getClienteNombre());
        if (dto.getClienteDni() != null) venta.setClienteDni(dto.getClienteDni());

        return toDTO(ventaRepository.save(venta));
    }

    @Transactional
    public void delete(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));
        venta.setEstado("ANULADA");
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
                        ? venta.getCliente().getNombre() + " " + venta.getCliente().getApellido()
                        : venta.getClienteNombre())
                .clienteDni(venta.getCliente() != null
                        ? venta.getCliente().getDni()
                        : venta.getClienteDni())
                .usuarioId(venta.getUsuario() != null ? venta.getUsuario().getId() : null)
                .usuarioNombre(venta.getUsuario() != null
                        ? venta.getUsuario().getNombre() + " " + venta.getUsuario().getApellido() : null)
                .fecha(venta.getFecha())
                .totalConDescuento(venta.getTotalConDescuento())
                .descuento(venta.getDescuento())
                .totalSinDescuento(venta.getTotalSinDescuento())
                .estado(venta.getEstado())
                .detalles(detallesDTO)
                .fechaCreacion(venta.getFechaCreacion())
                .fechaEdicion(venta.getFechaEdicion())
                .build();
    }

    private DetalleVentaDTO detalleToDTO(DetalleVenta detalle) {
        return DetalleVentaDTO.builder()
                .id(detalle.getId())
                .ventaId(detalle.getVenta() != null ? detalle.getVenta().getId() : null)
                .presentacionId(detalle.getPresentacion() != null ? detalle.getPresentacion().getId() : null)
                .presentacionNombre(detalle.getPresentacion() != null ? detalle.getPresentacion().getNombre() : null)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .fechaCreacion(detalle.getFechaCreacion())
                .fechaEdicion(detalle.getFechaEdicion())
                .build();
    }
}
