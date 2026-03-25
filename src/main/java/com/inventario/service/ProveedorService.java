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
                .razonSocial(dto.getRazonSocial())
                .ruc(dto.getRuc())
                .telefono(dto.getTelefono())
                .correo(dto.getCorreo())
                .direccion(dto.getDireccion())
                .avatar(dto.getAvatar() != null ? dto.getAvatar() : "proveedor.png")
                .estado("A")
                .build();
        return toDTO(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorDTO update(Long id, ProveedorDTO.Update dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));

        if (dto.getRazonSocial() != null) proveedor.setRazonSocial(dto.getRazonSocial());
        if (dto.getRuc() != null) proveedor.setRuc(dto.getRuc());
        if (dto.getTelefono() != null) proveedor.setTelefono(dto.getTelefono());
        if (dto.getCorreo() != null) proveedor.setCorreo(dto.getCorreo());
        if (dto.getDireccion() != null) proveedor.setDireccion(dto.getDireccion());
        if (dto.getAvatar() != null) proveedor.setAvatar(dto.getAvatar());

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
                .razonSocial(proveedor.getRazonSocial())
                .ruc(proveedor.getRuc())
                .telefono(proveedor.getTelefono())
                .correo(proveedor.getCorreo())
                .direccion(proveedor.getDireccion())
                .avatar(proveedor.getAvatar())
                .estado(proveedor.getEstado())
                .fechaCreacion(proveedor.getFechaCreacion())
                .fechaEdicion(proveedor.getFechaEdicion())
                .build();
    }
}
