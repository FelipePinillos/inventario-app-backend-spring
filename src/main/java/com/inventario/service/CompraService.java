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
    private final PresentacionRepository presentacionRepository;

    public List<CompraDTO> findAll(boolean incluirInactivos) {
        List<Compra> compras = incluirInactivos
                ? compraRepository.findAll()
                : compraRepository.findByEstado("CONFIRMADA");
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

        BigDecimal descuento = dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO;
        BigDecimal totalSinDescuento = calcularTotal(dto.getDetalles());
        BigDecimal totalConDescuento = totalSinDescuento.subtract(descuento);

        Compra compra = Compra.builder()
                .proveedor(proveedor)
                .usuario(usuario)
                .fechaCompra(dto.getFechaCompra())
                .fechaEntrega(dto.getFechaEntrega())
                .descuento(descuento)
                .totalSinDescuento(totalSinDescuento)
                .totalConDescuento(totalConDescuento)
                .estado("CONFIRMADA")
                .build();

        Compra savedCompra = compraRepository.save(compra);

        List<DetalleCompra> detalles = dto.getDetalles().stream().map(d -> {
            Presentacion presentacion = presentacionRepository.findById(d.getPresentacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Presentacion", d.getPresentacionId()));

            BigDecimal subtotal = d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
            return DetalleCompra.builder()
                    .compra(savedCompra)
                    .presentacion(presentacion)
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .subtotal(subtotal)
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
        if (dto.getFechaCompra() != null) compra.setFechaCompra(dto.getFechaCompra());
        if (dto.getFechaEntrega() != null) compra.setFechaEntrega(dto.getFechaEntrega());
        if (dto.getDescuento() != null) {
            compra.setDescuento(dto.getDescuento());
            if (compra.getTotalSinDescuento() != null) {
                compra.setTotalConDescuento(compra.getTotalSinDescuento().subtract(dto.getDescuento()));
            }
        }
        if (dto.getEstado() != null) compra.setEstado(dto.getEstado());

        return toDTO(compraRepository.save(compra));
    }

    @Transactional
    public void delete(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));
        compra.setEstado("ANULADA");
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
                .proveedorNombre(compra.getProveedor() != null ? compra.getProveedor().getRazonSocial() : null)
                .usuarioId(compra.getUsuario() != null ? compra.getUsuario().getId() : null)
                .usuarioNombre(compra.getUsuario() != null
                        ? compra.getUsuario().getNombre() + " " + compra.getUsuario().getApellido() : null)
                .fechaCompra(compra.getFechaCompra())
                .fechaEntrega(compra.getFechaEntrega())
                .totalConDescuento(compra.getTotalConDescuento())
                .descuento(compra.getDescuento())
                .totalSinDescuento(compra.getTotalSinDescuento())
                .estado(compra.getEstado())
                .detalles(detallesDTO)
                .fechaCreacion(compra.getFechaCreacion())
                .fechaEdicion(compra.getFechaEdicion())
                .build();
    }

    private DetalleCompraDTO detalleToDTO(DetalleCompra detalle) {
        return DetalleCompraDTO.builder()
                .id(detalle.getId())
                .compraId(detalle.getCompra() != null ? detalle.getCompra().getId() : null)
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
