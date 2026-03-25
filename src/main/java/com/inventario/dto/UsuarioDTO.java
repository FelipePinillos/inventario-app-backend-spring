package com.inventario.dto;

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
    private String dni;
    private Long tipoUsuarioId;
    private String tipoUsuarioNombre;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        @NotBlank(message = "El nombre es requerido")
        private String nombre;

        @NotBlank(message = "El apellido es requerido")
        private String apellido;

        @NotBlank(message = "El DNI es requerido")
        private String dni;

        @NotBlank(message = "La contraseña es requerida")
        private String contrasena;

        private Long tipoUsuarioId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private String nombre;
        private String apellido;
        private String dni;
        private String contrasena;
        private Long tipoUsuarioId;
    }
}
