package com.inventario.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder compatible con hashes $2b$ generados por Python (passlib/bcrypt).
 * Spring Security usa $2a$, pero $2b$ es equivalente — solo difiere el prefijo.
 * Este encoder convierte $2b$ → $2a$ antes de comparar.
 */
public class BCrypt2bPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        // Nuevas contraseñas se generan con $2a$ (estándar de Spring)
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        // Convertir $2b$ → $2a$ para que BCryptPasswordEncoder lo reconozca
        String normalizado = encodedPassword.replace("$2b$", "$2a$");
        return delegate.matches(rawPassword, normalizado);
    }
}

