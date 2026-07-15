package com.ufide.barbex.controller;

import com.ufide.barbex.entity.*;
import com.ufide.barbex.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CitaService citaService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private SolicitudCambioCitaService solicitudService;

    private Usuario getClienteSession(HttpSession session) {
        Usuario usuario = usuarioService.getUsuarioSession(session);
        if (usuario == null || !usuario.esCliente()) {
            throw new RuntimeException("Acceso denegado");
        }
        return usuario;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario cliente = getClienteSession(session);
        model.addAttribute("citas", citaService.listarPorCliente(cliente.getId()));
        model.addAttribute("solicitudes", solicitudService.listarPorCliente(cliente.getId()));
        return "cliente/dashboard";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCita(HttpSession session, Model model) {
        Usuario cliente = getClienteSession(session);
        Long barberiaId = cliente.getBarberia().getId();
        List<Usuario> barberos = usuarioService.listarBarberosPorBarberia(barberiaId);
        List<Servicio> servicios = servicioService.listarActivosPorBarberia(barberiaId);

        List<HorarioGeneral> horariosTodos = new ArrayList<>();
        List<ExcepcionHorario> excepcionesTodos = new ArrayList<>();
        for (Usuario b : barberos) {
            horariosTodos.addAll(horarioService.listarPorBarbero(b.getId()));
            excepcionesTodos.addAll(horarioService.listarExcepcionesFuturasPorBarbero(b.getId()));
        }

        model.addAttribute("barberos", barberos);
        model.addAttribute("servicios", servicios);
        model.addAttribute("barberiaId", barberiaId);
        model.addAttribute("clienteId", cliente.getId());
        model.addAttribute("horariosTodosJson", horarioGeneralToJson(horariosTodos));
        model.addAttribute("excepcionesTodosJson", excepcionesToJson(excepcionesTodos));
        model.addAttribute("serviciosJson", serviciosToJson(servicios));
        return "cliente/cita-form";
    }

    private String serviciosToJson(List<Servicio> servicios) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < servicios.size(); i++) {
            Servicio s = servicios.get(i);
            sb.append("{");
            sb.append("\"id\":").append(s.getId()).append(",");
            sb.append("\"nombre\":\"").append(s.getNombre()).append("\",");
            sb.append("\"precio\":").append(s.getPrecio()).append(",");
            sb.append("\"duracionMinutos\":").append(s.getDuracionMinutos()).append(",");
            sb.append("\"montoAdelanto\":").append(s.getMontoAdelanto() != null ? s.getMontoAdelanto() : "0");
            sb.append("}");
            if (i < servicios.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String horarioGeneralToJson(List<HorarioGeneral> horarios) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < horarios.size(); i++) {
            HorarioGeneral h = horarios.get(i);
            sb.append("{");
            sb.append("\"barberoId\":").append(h.getBarbero().getId()).append(",");
            sb.append("\"dia\":\"").append(h.getDiaSemana()).append("\",");
            sb.append("\"libre\":").append(h.isLibre()).append(",");
            sb.append("\"inicio\":").append(h.getHoraInicio() != null ? "\"" + h.getHoraInicio() + "\"" : "null").append(",");
            sb.append("\"fin\":").append(h.getHoraFin() != null ? "\"" + h.getHoraFin() + "\"" : "null");
            sb.append("}");
            if (i < horarios.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String excepcionesToJson(List<ExcepcionHorario> excepciones) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < excepciones.size(); i++) {
            ExcepcionHorario e = excepciones.get(i);
            sb.append("{");
            sb.append("\"barberoId\":").append(e.getBarbero().getId()).append(",");
            sb.append("\"fecha\":\"").append(e.getFecha()).append("\",");
            sb.append("\"tipo\":\"").append(e.getTipo()).append("\",");
            sb.append("\"inicio\":").append(e.getHoraInicio() != null ? "\"" + e.getHoraInicio() + "\"" : "null").append(",");
            sb.append("\"fin\":").append(e.getHoraFin() != null ? "\"" + e.getHoraFin() + "\"" : "null").append(",");
            sb.append("\"motivo\":\"").append(e.getMotivo() != null ? e.getMotivo() : "").append("\"");
            sb.append("}");
            if (i < excepciones.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    @PostMapping("/citas/guardar")
    public String guardarCita(@RequestParam Long barberoId,
                              @RequestParam Long clienteId,
                              @RequestParam Long barberiaId,
                              @RequestParam LocalDate fecha,
                              @RequestParam LocalTime horaInicio,
                              @RequestParam(required = false) List<Long> servicios,
                              @RequestParam(required = false, defaultValue = "false") boolean adelantoPagado,
                              @RequestParam(required = false) String comprobanteAdelanto,
                              RedirectAttributes ra) {
        try {
            citaService.crearCita(barberoId, clienteId, barberiaId, fecha, horaInicio, servicios, adelantoPagado, comprobanteAdelanto);
            ra.addFlashAttribute("ok", "Cita agendada correctamente");
        } catch (Exception e) {
            System.err.println("ERROR AL CREAR CITA: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/cliente/citas/nueva";
        }
        return "redirect:/cliente/dashboard";
    }

    @GetMapping("/citas/{id}/editar")
    public String editarCita(@PathVariable Long id, HttpSession session, Model model) {
        Usuario cliente = getClienteSession(session);
        Cita cita = citaService.findById(id).orElseThrow();
        if (!cita.getCliente().getId().equals(cliente.getId())) {
            return "redirect:/cliente/dashboard";
        }
        model.addAttribute("cita", cita);
        return "cliente/cita-editar";
    }

    @PostMapping("/citas/{id}/solicitar-edicion")
    public String solicitarEdicion(@PathVariable Long id,
                                   @RequestParam LocalDate nuevaFecha,
                                   @RequestParam LocalTime nuevaHoraInicio,
                                   RedirectAttributes ra) {
        try {
            solicitudService.solicitarEdicion(id, nuevaFecha, nuevaHoraInicio);
            ra.addFlashAttribute("ok", "Solicitud de edicion enviada");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cliente/dashboard";
    }

    @PostMapping("/citas/{id}/solicitar-cancelacion")
    public String solicitarCancelacion(@PathVariable Long id, RedirectAttributes ra) {
        try {
            solicitudService.solicitarCancelacion(id);
            ra.addFlashAttribute("ok", "Solicitud de cancelacion enviada");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cliente/dashboard";
    }
}
