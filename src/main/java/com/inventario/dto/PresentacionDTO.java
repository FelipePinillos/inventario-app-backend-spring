package com.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresentacionDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private String unidadMedida;
    private BigDecimal precio;
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

        private String descripcion;

        @NotNull(message = "El producto es requerido")
        private Long productoId;

        @NotNull(message = "La cantidad es requerida")
        private Integer cantidad;

        private String unidadMedida;

        @NotNull(message = "El precio es requerido")
        private BigDecimal precio;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private String nombre;
        private String descripcion;
        private Long productoId;
        private Integer cantidad;
        private String unidadMedida;
        private BigDecimal precio;
    }
}
