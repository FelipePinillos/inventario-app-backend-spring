package com.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private Integer telefono;
    private String correo;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        private String nombre;
        private String apellido;
        private String dni;

        @NotNull(message = "El teléfono es requerido")
        private Integer telefono;

        @NotBlank(message = "El correo es requerido")
        private String correo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private String nombre;
        private String apellido;
        private String dni;
        private Integer telefono;
        private String correo;
    }
}
