package com.inventario.service;

import com.inventario.dto.ProductoDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.*;
import com.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final TipoProductoRepository tipoProductoRepository;

    public List<ProductoDTO> findAll(boolean incluirInactivos) {
        List<Producto> productos = incluirInactivos
                ? productoRepository.findAll()
                : productoRepository.findByEstado("A");
        return productos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProductoDTO findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return toDTO(producto);
    }

    @Transactional
    public ProductoDTO create(ProductoDTO.Create dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", dto.getCategoriaId()));
        Marca marca = marcaRepository.findById(dto.getMarcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca", dto.getMarcaId()));
        TipoProducto tipoProducto = tipoProductoRepository.findById(dto.getTipoProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoProducto", dto.getTipoProductoId()));

        Producto producto = Producto.builder()
                .codigo(dto.getCodigo() != null ? dto.getCodigo() : "0")
                .nombre(dto.getNombre())
                .unidadBase(dto.getUnidadBase() != null ? dto.getUnidadBase() : "unidad")
                .adicional(dto.getAdicional())
                .stockMinimo(dto.getStockMinimo())
                .stockActual(dto.getStockActual() != null ? dto.getStockActual() : 0)
                .stockMaximo(dto.getStockMaximo())
                .avatar(dto.getAvatar() != null ? dto.getAvatar() : "")
                .categoria(categoria)
                .marca(marca)
                .tipoProducto(tipoProducto)
                .estado("A")
                .build();

        return toDTO(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO update(Long id, ProductoDTO.Update dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        if (dto.getCodigo() != null) producto.setCodigo(dto.getCodigo());
        if (dto.getNombre() != null) producto.setNombre(dto.getNombre());
        if (dto.getUnidadBase() != null) producto.setUnidadBase(dto.getUnidadBase());
        if (dto.getAdicional() != null) producto.setAdicional(dto.getAdicional());
        if (dto.getStockMinimo() != null) producto.setStockMinimo(dto.getStockMinimo());
        if (dto.getStockActual() != null) producto.setStockActual(dto.getStockActual());
        if (dto.getStockMaximo() != null) producto.setStockMaximo(dto.getStockMaximo());
        if (dto.getAvatar() != null) producto.setAvatar(dto.getAvatar());
        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", dto.getCategoriaId()));
            producto.setCategoria(categoria);
        }
        if (dto.getMarcaId() != null) {
            Marca marca = marcaRepository.findById(dto.getMarcaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Marca", dto.getMarcaId()));
            producto.setMarca(marca);
        }
        if (dto.getTipoProductoId() != null) {
            TipoProducto tipoProducto = tipoProductoRepository.findById(dto.getTipoProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("TipoProducto", dto.getTipoProductoId()));
            producto.setTipoProducto(tipoProducto);
        }

        return toDTO(productoRepository.save(producto));
    }

    @Transactional
    public void delete(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        producto.setEstado("I");
        productoRepository.save(producto);
    }

    private ProductoDTO toDTO(Producto producto) {
        return ProductoDTO.builder()
                .id(producto.getId())
                .codigo(producto.getCodigo())
                .nombre(producto.getNombre())
                .unidadBase(producto.getUnidadBase())
                .adicional(producto.getAdicional())
                .stockMinimo(producto.getStockMinimo())
                .stockActual(producto.getStockActual())
                .stockMaximo(producto.getStockMaximo())
                .avatar(producto.getAvatar())
                .categoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null)
                .categoriaNombre(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .marcaId(producto.getMarca() != null ? producto.getMarca().getId() : null)
                .marcaNombre(producto.getMarca() != null ? producto.getMarca().getNombre() : null)
                .tipoProductoId(producto.getTipoProducto() != null ? producto.getTipoProducto().getId() : null)
                .tipoProductoNombre(producto.getTipoProducto() != null ? producto.getTipoProducto().getNombre() : null)
                .estado(producto.getEstado())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaEdicion(producto.getFechaEdicion())
                .build();
    }
}
