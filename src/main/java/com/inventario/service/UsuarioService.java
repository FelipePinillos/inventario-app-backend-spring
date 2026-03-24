package com.inventario.service;

import com.inventario.dto.UsuarioDTO;
import com.inventario.exception.BadRequestException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.TipoUsuario;
import com.inventario.models.Usuario;
import com.inventario.repository.TipoUsuarioRepository;
import com.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> findAll(boolean incluirInactivos) {
        List<Usuario> usuarios = incluirInactivos
                ? usuarioRepository.findAll()
                : usuarioRepository.findByEstado("A");
        return usuarios.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO create(UsuarioDTO.Create dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        TipoUsuario tipoUsuario = null;
        if (dto.getTipoUsuarioId() != null) {
            tipoUsuario = tipoUsuarioRepository.findById(dto.getTipoUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("TipoUsuario", dto.getTipoUsuarioId()));
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .tipoUsuario(tipoUsuario)
                .estado("A")
                .build();

        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO update(Long id, UsuarioDTO.Update dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getApellido() != null) usuario.setApellido(dto.getApellido());
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(usuario.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new BadRequestException("Ya existe un usuario con el email: " + dto.getEmail());
            }
            usuario.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getTipoUsuarioId() != null) {
            TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(dto.getTipoUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("TipoUsuario", dto.getTipoUsuarioId()));
            usuario.setTipoUsuario(tipoUsuario);
        }

        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        usuario.setEstado("I");
        usuarioRepository.save(usuario);
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .tipoUsuarioId(usuario.getTipoUsuario() != null ? usuario.getTipoUsuario().getId() : null)
                .tipoUsuarioNombre(usuario.getTipoUsuario() != null ? usuario.getTipoUsuario().getNombre() : null)
                .estado(usuario.getEstado())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
