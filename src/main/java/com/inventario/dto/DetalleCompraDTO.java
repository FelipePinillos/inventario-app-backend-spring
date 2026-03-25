package com.inventario.dto;

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
public class DetalleCompraDTO {
    private Long id;
    private Long compraId;
    private Long presentacionId;
    private String presentacionNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        @NotNull(message = "La presentación es requerida")
        private Long presentacionId;

        @NotNull(message = "La cantidad es requerida")
        private Integer cantidad;

        @NotNull(message = "El precio unitario es requerido")
        private BigDecimal precioUnitario;
    }
}
