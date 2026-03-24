package com.inventario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Long tipoUsuarioId;
    private String tipoUsuarioNombre;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        @NotBlank(message = "El nombre es requerido")
        private String nombre;

        @NotBlank(message = "El apellido es requerido")
        private String apellido;

        @NotBlank(message = "El email es requerido")
        @Email(message = "El email debe ser válido")
        private String email;

        @NotBlank(message = "La contraseña es requerida")
        private String password;

        private Long tipoUsuarioId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private String nombre;
        private String apellido;
        @Email(message = "El email debe ser válido")
        private String email;
        private String password;
        private Long tipoUsuarioId;
    }
}
