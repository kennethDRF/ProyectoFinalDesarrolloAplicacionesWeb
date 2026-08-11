package com.ufide.barbex.controller;

import com.ufide.barbex.dto.ServicioDto;
import com.ufide.barbex.entity.Barberia;
import com.ufide.barbex.entity.Servicio;
import com.ufide.barbex.service.BarberiaService;
import com.ufide.barbex.service.ServicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioRestController {

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private BarberiaService barberiaService;

    @GetMapping
    public List<ServicioDto> listar(@RequestParam(required = false) Long barberiaId) {
        List<Servicio> servicios = (barberiaId != null)
                ? servicioService.listarPorBarberia(barberiaId)
                : servicioService.listarTodas();
        return servicios.stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioDto> obtener(@PathVariable Long id) {
        return servicioService.findById(id)
                .map(s -> ResponseEntity.ok(toDto(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServicioDto> crear(@Valid @RequestBody ServicioDto dto, UriComponentsBuilder ucb) {
        Barberia barberia = barberiaService.findById(dto.barberiaId()).orElse(null);
        if (barberia == null) {
            return ResponseEntity.badRequest().build();
        }
        Servicio servicio = new Servicio();
        servicio.setNombre(dto.nombre());
        servicio.setDescripcion(dto.descripcion());
        servicio.setPrecio(dto.precio());
        servicio.setDuracionMinutos(dto.duracionMinutos());
        servicio.setMontoAdelanto(dto.montoAdelanto());
        servicio.setActivo(dto.activo());
        servicio.setBarberia(barberia);

        Servicio guardado = servicioService.guardar(servicio);
        URI location = ucb.path("/api/servicios/{id}").buildAndExpand(guardado.getId()).toUri();
        return ResponseEntity.created(location).body(toDto(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioDto> actualizar(@PathVariable Long id, @Valid @RequestBody ServicioDto dto) {
        Servicio servicio = servicioService.findById(id).orElse(null);
        if (servicio == null) {
            return ResponseEntity.notFound().build();
        }
        servicio.setNombre(dto.nombre());
        servicio.setDescripcion(dto.descripcion());
        servicio.setPrecio(dto.precio());
        servicio.setDuracionMinutos(dto.duracionMinutos());
        servicio.setMontoAdelanto(dto.montoAdelanto());
        servicio.setActivo(dto.activo());
        if (dto.barberiaId() != null) {
            Barberia barberia = barberiaService.findById(dto.barberiaId()).orElse(null);
            if (barberia == null) {
                return ResponseEntity.badRequest().build();
            }
            servicio.setBarberia(barberia);
        }

        return ResponseEntity.ok(toDto(servicioService.guardar(servicio)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (servicioService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            servicioService.eliminarDefinitivo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private ServicioDto toDto(Servicio s) {
        return new ServicioDto(
                s.getId(),
                s.getNombre(),
                s.getDescripcion(),
                s.getPrecio(),
                s.getDuracionMinutos(),
                s.getMontoAdelanto(),
                s.isActivo(),
                s.getBarberia().getId(),
                s.getBarberia().getNombre()
        );
    }
}
