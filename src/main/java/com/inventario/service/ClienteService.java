package com.inventario.service;

import com.inventario.dto.ClienteDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.Cliente;
import com.inventario.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteDTO> findAll(boolean incluirInactivos) {
        List<Cliente> clientes = incluirInactivos
                ? clienteRepository.findAll()
                : clienteRepository.findByEstado("A");
        return clientes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ClienteDTO findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        return toDTO(cliente);
    }

    @Transactional
    public ClienteDTO create(ClienteDTO.Create dto) {
        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .estado("A")
                .build();
        return toDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDTO update(Long id, ClienteDTO.Update dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));

        if (dto.getNombre() != null) cliente.setNombre(dto.getNombre());
        if (dto.getApellido() != null) cliente.setApellido(dto.getApellido());
        if (dto.getEmail() != null) cliente.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) cliente.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) cliente.setDireccion(dto.getDireccion());

        return toDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public void delete(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        cliente.setEstado("I");
        clienteRepository.save(cliente);
    }

    private ClienteDTO toDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .estado(cliente.getEstado())
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .build();
    }
}
