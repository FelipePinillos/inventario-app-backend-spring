package com.inventario.service;

import com.inventario.dto.MarcaDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.Marca;
import com.inventario.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public List<MarcaDTO> findAll(boolean incluirInactivos) {
        List<Marca> marcas = incluirInactivos
                ? marcaRepository.findAll()
                : marcaRepository.findByEstado("A");
        return marcas.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MarcaDTO findById(Long id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca", id));
        return toDTO(marca);
    }

    @Transactional
    public MarcaDTO create(MarcaDTO.Create dto) {
        Marca marca = Marca.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .estado("A")
                .build();
        return toDTO(marcaRepository.save(marca));
    }

    @Transactional
    public MarcaDTO update(Long id, MarcaDTO.Update dto) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca", id));

        if (dto.getNombre() != null) marca.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) marca.setDescripcion(dto.getDescripcion());

        return toDTO(marcaRepository.save(marca));
    }

    @Transactional
    public void delete(Long id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca", id));
        marca.setEstado("I");
        marcaRepository.save(marca);
    }

    private MarcaDTO toDTO(Marca marca) {
        return MarcaDTO.builder()
                .id(marca.getId())
                .nombre(marca.getNombre())
                .descripcion(marca.getDescripcion())
                .estado(marca.getEstado())
                .createdAt(marca.getCreatedAt())
                .updatedAt(marca.getUpdatedAt())
                .build();
    }
}
