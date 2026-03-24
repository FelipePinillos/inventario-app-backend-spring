package com.inventario.controller;

import com.inventario.dto.ProveedorDTO;
import com.inventario.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> findAll(
            @RequestParam(defaultValue = "false") boolean incluir_inactivos) {
        return ResponseEntity.ok(proveedorService.findAll(incluir_inactivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> create(@Valid @RequestBody ProveedorDTO.Create dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> update(@PathVariable Long id,
                                                @RequestBody ProveedorDTO.Update dto) {
        return ResponseEntity.ok(proveedorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proveedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
