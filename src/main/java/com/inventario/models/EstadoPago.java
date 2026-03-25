package com.inventario.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estado_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String nombre;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;
}
