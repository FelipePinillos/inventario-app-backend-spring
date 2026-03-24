package com.inventario.service;

import com.inventario.dto.TipoProductoDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.TipoProducto;
import com.inventario.repository.TipoProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoProductoService {

    private final TipoProductoRepository tipoProductoRepository;

    public List<TipoProductoDTO> findAll(boolean incluirInactivos) {
        List<TipoProducto> tipos = incluirInactivos
                ? tipoProductoRepository.findAll()
                : tipoProductoRepository.findByEstado("A");
        return tipos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TipoProductoDTO findById(Long id) {
        TipoProducto tipo = tipoProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoProducto", id));
        return toDTO(tipo);
    }

    @Transactional
    public TipoProductoDTO create(TipoProductoDTO.Create dto) {
        TipoProducto tipo = TipoProducto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .estado("A")
                .build();
        return toDTO(tipoProductoRepository.save(tipo));
    }

    @Transactional
    public TipoProductoDTO update(Long id, TipoProductoDTO.Update dto) {
        TipoProducto tipo = tipoProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoProducto", id));

        if (dto.getNombre() != null) tipo.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) tipo.setDescripcion(dto.getDescripcion());

        return toDTO(tipoProductoRepository.save(tipo));
    }

    @Transactional
    public void delete(Long id) {
        TipoProducto tipo = tipoProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoProducto", id));
        tipo.setEstado("I");
        tipoProductoRepository.save(tipo);
    }

    private TipoProductoDTO toDTO(TipoProducto tipo) {
        return TipoProductoDTO.builder()
                .id(tipo.getId())
                .nombre(tipo.getNombre())
                .descripcion(tipo.getDescripcion())
                .estado(tipo.getEstado())
                .createdAt(tipo.getCreatedAt())
                .updatedAt(tipo.getUpdatedAt())
                .build();
    }
}
