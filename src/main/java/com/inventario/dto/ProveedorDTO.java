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
public class ProveedorDTO {
    private Long id;
    private String razonSocial;
    private Long ruc;
    private Integer telefono;
    private String correo;
    private String direccion;
    private String avatar;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        @NotBlank(message = "La razón social es requerida")
        private String razonSocial;
        @NotNull(message = "El RUC es requerido")
        private Long ruc;
        private Integer telefono;
        @NotBlank(message = "El correo es requerido")
        private String correo;
        @NotBlank(message = "La dirección es requerida")
        private String direccion;
        private String avatar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private String razonSocial;
        private Long ruc;
        private Integer telefono;
        private String correo;
        private String direccion;
        private String avatar;
    }
}
