package com.medtriage.controller;

import com.medtriage.dto.CitaMedicaDTO;
import com.medtriage.dto.CrearCitaDTO;
import com.medtriage.service.CitaMedicaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/citas")
@CrossOrigin(origins = "http://localhost:4200")
public class CitaMedicaController {

    private final CitaMedicaService citaService;

    public CitaMedicaController(CitaMedicaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public ResponseEntity<List<CitaMedicaDTO>> obtenerTodas() {

        return ResponseEntity.ok(
                citaService.obtenerTodas());
    }

    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<CitaMedicaDTO>> obtenerPorPrioridad(
            @PathVariable String prioridad) {

        return ResponseEntity.ok(
                citaService.obtenerPorPrioridad(prioridad));
    }

    @PostMapping
    public ResponseEntity<CitaMedicaDTO> crearCita(
            @RequestBody CrearCitaDTO dto) {

        CitaMedicaDTO citaCreada = citaService.crearCita(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(citaCreada);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CitaMedicaDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estado");

        CitaMedicaDTO citaActualizada = citaService.actualizarEstado(id, nuevoEstado);

        return ResponseEntity.ok(citaActualizada);
    }
}