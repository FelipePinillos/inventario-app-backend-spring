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
public class ProductoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String unidadBase;
    private String adicional;
    private Integer stockMinimo;
    private Integer stockActual;
    private Integer stockMaximo;
    private String avatar;
    private Long categoriaId;
    private String categoriaNombre;
    private Long marcaId;
    private String marcaNombre;
    private Long tipoProductoId;
    private String tipoProductoNombre;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        private String codigo;

        @NotBlank(message = "El nombre es requerido")
        private String nombre;

        private String unidadBase;
        private String adicional;
        private Integer stockMinimo;
        private Integer stockActual;
        private Integer stockMaximo;
        private String avatar;

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
        private String codigo;
        private String nombre;
        private String unidadBase;
        private String adicional;
        private Integer stockMinimo;
        private Integer stockActual;
        private Integer stockMaximo;
        private String avatar;
        private Long categoriaId;
        private Long marcaId;
        private Long tipoProductoId;
    }
}
