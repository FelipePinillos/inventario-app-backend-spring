package com.inventario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private Long usuarioId;
    private String usuarioNombre;
    private Long estadoPagoId;
    private String estadoPagoNombre;
    private LocalDate fecha;
    private BigDecimal total;
    private String observaciones;
    private String estado;
    private List<DetalleVentaDTO> detalles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        private Long clienteId;

        @NotNull(message = "El usuario es requerido")
        private Long usuarioId;

        @NotNull(message = "El estado de pago es requerido")
        private Long estadoPagoId;

        @NotNull(message = "La fecha es requerida")
        private LocalDate fecha;

        private String observaciones;

        @NotNull(message = "Los detalles son requeridos")
        private List<DetalleVentaDTO.Create> detalles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private Long clienteId;
        private Long usuarioId;
        private Long estadoPagoId;
        private LocalDate fecha;
        private String observaciones;
        private List<DetalleVentaDTO.Create> detalles;
    }
}
