package com.inventario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraDTO {
    private Long id;
    private Long proveedorId;
    private String proveedorNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaCompra;
    private LocalDateTime fechaEntrega;
    private BigDecimal totalConDescuento;
    private BigDecimal descuento;
    private BigDecimal totalSinDescuento;
    private String estado;
    private List<DetalleCompraDTO> detalles;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        @NotNull(message = "El proveedor es requerido")
        private Long proveedorId;

        @NotNull(message = "El usuario es requerido")
        private Long usuarioId;

        private LocalDateTime fechaCompra;
        private LocalDateTime fechaEntrega;
        private BigDecimal descuento;

        @NotNull(message = "Los detalles son requeridos")
        private List<DetalleCompraDTO.Create> detalles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private Long proveedorId;
        private Long usuarioId;
        private LocalDateTime fechaCompra;
        private LocalDateTime fechaEntrega;
        private BigDecimal descuento;
        private String estado;
    }
}
