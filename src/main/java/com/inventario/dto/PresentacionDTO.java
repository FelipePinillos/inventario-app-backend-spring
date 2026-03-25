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
    private Long productoId;
    private String productoNombre;
    private Integer cantidadBase;
    private BigDecimal precioVenta;
    private BigDecimal precioCompra;
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

        @NotNull(message = "El producto es requerido")
        private Long productoId;

        @NotNull(message = "La cantidad base es requerida")
        private Integer cantidadBase;

        @NotNull(message = "El precio de venta es requerido")
        private BigDecimal precioVenta;

        private BigDecimal precioCompra;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private String nombre;
        private Long productoId;
        private Integer cantidadBase;
        private BigDecimal precioVenta;
        private BigDecimal precioCompra;
    }
}
