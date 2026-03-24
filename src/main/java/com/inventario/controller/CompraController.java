package com.inventario.controller;

import com.inventario.dto.CompraDTO;
import com.inventario.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public ResponseEntity<List<CompraDTO>> findAll(
            @RequestParam(name = "incluir_inactivos", defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(compraService.findAll(incluirInactivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CompraDTO> create(@Valid @RequestBody CompraDTO.Create dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompraDTO> update(@PathVariable Long id,
                                             @RequestBody CompraDTO.Update dto) {
        return ResponseEntity.ok(compraService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
