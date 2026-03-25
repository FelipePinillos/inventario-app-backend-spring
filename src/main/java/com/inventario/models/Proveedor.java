package com.inventario.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social", length = 100)
    private String razonSocial;

    @Column(nullable = false)
    private Long ruc;

    private Integer telefono;

    @Column(nullable = false, length = 45)
    private String correo;

    @Column(nullable = false, length = 45)
    private String direccion;

    @Column(nullable = false, length = 255)
    @Builder.Default
    private String avatar = "proveedor.png";

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String estado = "A";

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_edicion")
    private LocalDateTime fechaEdicion;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;
}
