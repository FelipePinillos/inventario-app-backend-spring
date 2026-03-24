package com.inventario.service;

import com.inventario.dto.AuthRequestDTO;
import com.inventario.dto.AuthResponseDTO;
import com.inventario.dto.UsuarioDTO;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.models.Usuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;

    public AuthResponseDTO login(AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return AuthResponseDTO.of(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTipoUsuario() != null ? usuario.getTipoUsuario().getNombre() : null
        );
    }

    public UsuarioDTO getCurrentUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

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
