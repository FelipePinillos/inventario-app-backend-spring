package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<String> errors;

    public static ErrorResponseDTO of(int status, String message) {
        return ErrorResponseDTO.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponseDTO of(int status, String message, List<String> errors) {
        return ErrorResponseDTO.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }
}
