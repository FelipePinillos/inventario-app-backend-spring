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
public class VentaDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteDni;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fecha;
    private BigDecimal totalConDescuento;
    private BigDecimal descuento;
    private BigDecimal totalSinDescuento;
    private String estado;
    private List<DetalleVentaDTO> detalles;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        private Long clienteId;
        private String clienteNombre;
        private String clienteDni;

        @NotNull(message = "El usuario es requerido")
        private Long usuarioId;

        @NotNull(message = "La fecha es requerida")
        private LocalDateTime fecha;

        private BigDecimal descuento;

        @NotNull(message = "Los detalles son requeridos")
        private List<DetalleVentaDTO.Create> detalles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private Long clienteId;
        private String clienteNombre;
        private String clienteDni;
        private Long usuarioId;
        private LocalDateTime fecha;
        private BigDecimal descuento;
        private String estado;
    }
}
