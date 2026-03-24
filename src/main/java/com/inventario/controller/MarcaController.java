package com.inventario.controller;

import com.inventario.dto.MarcaDTO;
import com.inventario.service.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<List<MarcaDTO>> findAll(
            @RequestParam(defaultValue = "false") boolean incluir_inactivos) {
        return ResponseEntity.ok(marcaService.findAll(incluir_inactivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MarcaDTO> create(@Valid @RequestBody MarcaDTO.Create dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marcaService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarcaDTO> update(@PathVariable Long id,
                                            @RequestBody MarcaDTO.Update dto) {
        return ResponseEntity.ok(marcaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        marcaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
