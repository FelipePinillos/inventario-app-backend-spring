package com.inventario.security;

import com.inventario.models.Usuario;
import com.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con DNI: " + dni));

        if (!"A".equals(usuario.getEstado())) {
            throw new UsernameNotFoundException("Usuario inactivo: " + dni);
        }

        String role = usuario.getTipoUsuario() != null
                ? usuario.getTipoUsuario().getNombre().toUpperCase()
                : "USER";

        return new User(
                usuario.getDni(),
                usuario.getContrasena(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
