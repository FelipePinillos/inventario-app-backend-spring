package com.inventario.service;

import com.inventario.dto.ProveedorDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.Proveedor;
import com.inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public List<ProveedorDTO> findAll(boolean incluirInactivos) {
        List<Proveedor> proveedores = incluirInactivos
                ? proveedorRepository.findAll()
                : proveedorRepository.findByEstado("A");
        return proveedores.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProveedorDTO findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        return toDTO(proveedor);
    }

    @Transactional
    public ProveedorDTO create(ProveedorDTO.Create dto) {
        Proveedor proveedor = Proveedor.builder()
                .nombre(dto.getNombre())
                .contacto(dto.getContacto())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .direccion(dto.getDireccion())
                .estado("A")
                .build();
        return toDTO(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorDTO update(Long id, ProveedorDTO.Update dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));

        if (dto.getNombre() != null) proveedor.setNombre(dto.getNombre());
        if (dto.getContacto() != null) proveedor.setContacto(dto.getContacto());
        if (dto.getTelefono() != null) proveedor.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) proveedor.setEmail(dto.getEmail());
        if (dto.getDireccion() != null) proveedor.setDireccion(dto.getDireccion());

        return toDTO(proveedorRepository.save(proveedor));
    }

    @Transactional
    public void delete(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        proveedor.setEstado("I");
        proveedorRepository.save(proveedor);
    }

    private ProveedorDTO toDTO(Proveedor proveedor) {
        return ProveedorDTO.builder()
                .id(proveedor.getId())
                .nombre(proveedor.getNombre())
                .contacto(proveedor.getContacto())
                .telefono(proveedor.getTelefono())
                .email(proveedor.getEmail())
                .direccion(proveedor.getDireccion())
                .estado(proveedor.getEstado())
                .createdAt(proveedor.getCreatedAt())
                .updatedAt(proveedor.getUpdatedAt())
                .build();
    }
}
