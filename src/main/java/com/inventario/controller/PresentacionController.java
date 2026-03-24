package com.inventario.controller;

import com.inventario.dto.PresentacionDTO;
import com.inventario.service.PresentacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/presentaciones")
@RequiredArgsConstructor
public class PresentacionController {

    private final PresentacionService presentacionService;

    @GetMapping
    public ResponseEntity<List<PresentacionDTO>> findAll(
            @RequestParam(name = "incluir_inactivos", defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(presentacionService.findAll(incluirInactivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresentacionDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(presentacionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PresentacionDTO> create(@Valid @RequestBody PresentacionDTO.Create dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(presentacionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresentacionDTO> update(@PathVariable Long id,
                                                   @RequestBody PresentacionDTO.Update dto) {
        return ResponseEntity.ok(presentacionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        presentacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
