package com.inventario.controller;

import com.inventario.dto.AuthRequestDTO;
import com.inventario.dto.UsuarioDTO;
import com.inventario.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // OAuth2 Password Flow desde Swagger (form-urlencoded)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        return doLogin(username, password);
    }

    // Login con JSON (Postman / fetch)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginJson(@RequestBody AuthRequestDTO request) {
        return doLogin(request.getUsername(), request.getPassword());
    }

    // Login con query params (Swagger UI envía así cuando usa OAuth2)
    @PostMapping(value = "/login", params = {"username", "password"})
    public ResponseEntity<?> loginQueryParams(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        return doLogin(username, password);
    }

    private ResponseEntity<?> doLogin(String username, String password) {
        try {
            AuthRequestDTO request = AuthRequestDTO.builder()
                    .username(username)
                    .password(password)
                    .build();
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("detail", "Credenciales inválidas"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getCurrentUser(userDetails.getUsername()));
    }
}
