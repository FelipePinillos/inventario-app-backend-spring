package com.inventario.service;

import com.inventario.dto.CategoriaDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.Categoria;
import com.inventario.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> findAll(boolean incluirInactivos) {
        List<Categoria> categorias = incluirInactivos
                ? categoriaRepository.findAll()
                : categoriaRepository.findByEstado("A");
        return categorias.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CategoriaDTO findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
        return toDTO(categoria);
    }

    @Transactional
    public CategoriaDTO create(CategoriaDTO.Create dto) {
        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .estado("A")
                .build();
        return toDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaDTO update(Long id, CategoriaDTO.Update dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));

        if (dto.getNombre() != null) categoria.setNombre(dto.getNombre());

        return toDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public void delete(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
        categoria.setEstado("I");
        categoriaRepository.save(categoria);
    }

    private CategoriaDTO toDTO(Categoria categoria) {
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .estado(categoria.getEstado())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaEdicion(categoria.getFechaEdicion())
                .build();
    }
}
