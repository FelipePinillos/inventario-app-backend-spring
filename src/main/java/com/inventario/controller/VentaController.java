package com.inventario.controller;

import com.inventario.dto.VentaDTO;
import com.inventario.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaDTO>> findAll(
            @RequestParam(name = "incluir_inactivos", defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(ventaService.findAll(incluirInactivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VentaDTO> create(@Valid @RequestBody VentaDTO.Create dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaDTO> update(@PathVariable Long id,
                                            @RequestBody VentaDTO.Update dto) {
        return ResponseEntity.ok(ventaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ventaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
