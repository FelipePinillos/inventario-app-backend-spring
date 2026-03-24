package com.inventario.controller;

import com.inventario.dto.TipoProductoDTO;
import com.inventario.service.TipoProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-producto")
@RequiredArgsConstructor
public class TipoProductoController {

    private final TipoProductoService tipoProductoService;

    @GetMapping
    public ResponseEntity<List<TipoProductoDTO>> findAll(
            @RequestParam(name = "incluir_inactivos", defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(tipoProductoService.findAll(incluirInactivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoProductoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoProductoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TipoProductoDTO> create(@Valid @RequestBody TipoProductoDTO.Create dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoProductoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoProductoDTO> update(@PathVariable Long id,
                                                   @RequestBody TipoProductoDTO.Update dto) {
        return ResponseEntity.ok(tipoProductoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoProductoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
