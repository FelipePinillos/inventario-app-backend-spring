package com.inventario.service;

import com.inventario.dto.ProductoDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.Categoria;
import com.inventario.models.Marca;
import com.inventario.models.Producto;
import com.inventario.models.TipoProducto;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.MarcaRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.TipoProductoRepository;
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
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precioCompra(dto.getPrecioCompra())
                .precioVenta(dto.getPrecioVenta())
                .stock(dto.getStock() != null ? dto.getStock() : 0)
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

        if (dto.getNombre() != null) producto.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) producto.setDescripcion(dto.getDescripcion());
        if (dto.getPrecioCompra() != null) producto.setPrecioCompra(dto.getPrecioCompra());
        if (dto.getPrecioVenta() != null) producto.setPrecioVenta(dto.getPrecioVenta());
        if (dto.getStock() != null) producto.setStock(dto.getStock());
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
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precioCompra(producto.getPrecioCompra())
                .precioVenta(producto.getPrecioVenta())
                .stock(producto.getStock())
                .categoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null)
                .categoriaNombre(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .marcaId(producto.getMarca() != null ? producto.getMarca().getId() : null)
                .marcaNombre(producto.getMarca() != null ? producto.getMarca().getNombre() : null)
                .tipoProductoId(producto.getTipoProducto() != null ? producto.getTipoProducto().getId() : null)
                .tipoProductoNombre(producto.getTipoProducto() != null ? producto.getTipoProducto().getNombre() : null)
                .estado(producto.getEstado())
                .createdAt(producto.getCreatedAt())
                .updatedAt(producto.getUpdatedAt())
                .build();
    }
}
