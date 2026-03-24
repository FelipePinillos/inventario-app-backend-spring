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
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stock;
    private Long categoriaId;
    private String categoriaNombre;
    private Long marcaId;
    private String marcaNombre;
    private Long tipoProductoId;
    private String tipoProductoNombre;
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

        @NotNull(message = "El precio de compra es requerido")
        private BigDecimal precioCompra;

        @NotNull(message = "El precio de venta es requerido")
        private BigDecimal precioVenta;

        private Integer stock;

        @NotNull(message = "La categoría es requerida")
        private Long categoriaId;

        @NotNull(message = "La marca es requerida")
        private Long marcaId;

        @NotNull(message = "El tipo de producto es requerido")
        private Long tipoProductoId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private String nombre;
        private String descripcion;
        private BigDecimal precioCompra;
        private BigDecimal precioVenta;
        private Integer stock;
        private Long categoriaId;
        private Long marcaId;
        private Long tipoProductoId;
    }
}
