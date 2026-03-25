package com.inventario.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "ok");
        response.put("app", "Inventario API");

        try (Connection conn = dataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            boolean valid = conn.isValid(3);
            response.put("database", valid ? "conectado ✓" : "error");
            response.put("db_url", url.replaceAll(":[^@]*@", ":***@")); // oculta la contraseña
            response.put("db_product", conn.getMetaData().getDatabaseProductName());
            response.put("db_version", conn.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            response.put("database", "error");
            response.put("db_error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        return ResponseEntity.ok(Map.of("message", "Inventario API - visita /health para ver estado"));
    }
}
