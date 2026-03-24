package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String type;
    private Long userId;
    private String nombre;
    private String apellido;
    private String email;
    private String tipoUsuario;

    public static AuthResponseDTO of(String token, Long userId, String nombre, String apellido, String email, String tipoUsuario) {
        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .userId(userId)
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .tipoUsuario(tipoUsuario)
                .build();
    }
}
