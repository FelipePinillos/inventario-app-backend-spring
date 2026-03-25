package com.inventario.config;

import com.inventario.models.TipoUsuario;
import com.inventario.models.Usuario;
import com.inventario.repository.TipoUsuarioRepository;
import com.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!postgres & !prod")
public class DataInitializer implements CommandLineRunner {

    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Crear tipo de usuario ADMIN si no existe
        TipoUsuario tipoAdmin = tipoUsuarioRepository.findByNombre("ADMIN")
                .orElseGet(() -> tipoUsuarioRepository.save(
                        TipoUsuario.builder().nombre("ADMIN").estado("A").build()
                ));

        // Crear tipo de usuario USER si no existe
        tipoUsuarioRepository.findByNombre("USER")
                .orElseGet(() -> tipoUsuarioRepository.save(
                        TipoUsuario.builder().nombre("USER").estado("A").build()
                ));

        // Crear usuario admin si no existe
        if (!usuarioRepository.existsByDni("00000000")) {
            Usuario admin = Usuario.builder()
                    .nombre("Admin")
                    .apellido("Sistema")
                    .dni("00000000")
                    .contrasena(passwordEncoder.encode("admin123"))
                    .tipoUsuario(tipoAdmin)
                    .estado("A")
                    .build();
            usuarioRepository.save(admin);
            log.info("========================================");
            log.info("Usuario admin creado:");
            log.info("  DNI (username): 00000000");
            log.info("  Password:       admin123");
            log.info("========================================");
        }
    }
}
