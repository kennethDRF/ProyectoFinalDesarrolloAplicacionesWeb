package com.ufide.barbex.controller;

import com.ufide.barbex.entity.*;
import com.ufide.barbex.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/barbero")
public class BarberoController {

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

    private Usuario getBarberoSession(HttpSession session) {
        Usuario usuario = usuarioService.getUsuarioSession(session);
        if (usuario == null || !usuario.esBarbero()) {
            throw new RuntimeException("Acceso denegado");
        }
        return usuario;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario barbero = getBarberoSession(session);
        List<Cita> citas = citaService.listarPorBarbero(barbero.getId());

        long totalPendientes = 0;
        long totalConfirmadas = 0;
        long totalCompletadas = 0;
        for (Cita c : citas) {
            if (c.getEstado() == EstadoCita.PENDIENTE) totalPendientes++;
            else if (c.getEstado() == EstadoCita.CONFIRMADA) totalConfirmadas++;
            else if (c.getEstado() == EstadoCita.COMPLETADA) totalCompletadas++;
        }

        model.addAttribute("citas", citas);
        model.addAttribute("solicitudesPendientes", solicitudService.listarPendientesPorBarbero(barbero.getId()));
        model.addAttribute("totalPendientes", totalPendientes);
        model.addAttribute("totalConfirmadas", totalConfirmadas);
        model.addAttribute("totalCompletadas", totalCompletadas);
        return "barbero/dashboard";
    }

    @PostMapping("/citas/{id}/completar")
    public String completarCita(@PathVariable Long id, RedirectAttributes ra) {
        citaService.marcarCompletada(id);
        ra.addFlashAttribute("ok", "Cita marcada como completada");
        return "redirect:/barbero/dashboard";
    }

    @PostMapping("/citas/{id}/cancelar")
    public String cancelarCita(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra) {
        citaService.cancelarDirectamente(id, motivo);
        ra.addFlashAttribute("ok", "Cita cancelada");
        return "redirect:/barbero/dashboard";
    }

    @PostMapping("/citas/{id}/aprobar-adelanto")
    public String aprobarAdelanto(@PathVariable Long id, RedirectAttributes ra) {
        try {
            citaService.aprobarAdelanto(id);
            ra.addFlashAttribute("ok", "Adelanto aprobado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/dashboard";
    }

    @PostMapping("/citas/{id}/rechazar")
    public String rechazarCita(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra) {
        citaService.rechazarCita(id, motivo);
        ra.addFlashAttribute("ok", "Cita rechazada");
        return "redirect:/barbero/dashboard";
    }

    @GetMapping("/citas/{id}/editar")
    public String editarCita(@PathVariable Long id, HttpSession session, Model model) {
        Usuario barbero = getBarberoSession(session);
        Cita cita = citaService.findById(id).orElseThrow();
        if (!cita.getBarbero().getId().equals(barbero.getId())) {
            return "redirect:/barbero/dashboard";
        }
        model.addAttribute("cita", cita);
        return "barbero/cita-editar";
    }

    @PostMapping("/citas/{id}/editar")
    public String actualizarCita(@PathVariable Long id,
                                 @RequestParam LocalDate nuevaFecha,
                                 @RequestParam LocalTime nuevaHoraInicio,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Usuario barbero = getBarberoSession(session);
        Cita cita = citaService.findById(id).orElseThrow();
        if (!cita.getBarbero().getId().equals(barbero.getId())) {
            return "redirect:/barbero/dashboard";
        }
        try {
            citaService.editarCita(id, nuevaFecha, nuevaHoraInicio);
            ra.addFlashAttribute("ok", "Cita actualizada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/dashboard";
    }

    @GetMapping("/servicios")
    public String servicios(HttpSession session, Model model) {
        Usuario barbero = getBarberoSession(session);
        model.addAttribute("servicios", servicioService.listarPorBarberia(barbero.getBarberia().getId()));
        model.addAttribute("barbero", barbero);
        return "barbero/servicios";
    }

    @GetMapping("/servicios/nuevo")
    public String nuevoServicio(HttpSession session, Model model) {
        Servicio servicio = new Servicio();
        servicio.setBarberia(getBarberoSession(session).getBarberia());
        model.addAttribute("servicio", servicio);
        return "barbero/servicio-form";
    }

    @PostMapping("/servicios/guardar")
    public String guardarServicio(@ModelAttribute Servicio servicio, HttpSession session, Model model, RedirectAttributes ra) {
        servicio.setBarberia(getBarberoSession(session).getBarberia());
        try {
            servicioService.guardar(servicio);
            ra.addFlashAttribute("ok", "Servicio guardado");
            return "redirect:/barbero/servicios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("servicio", servicio);
            return "barbero/servicio-form";
        }
    }

    @PostMapping("/servicios/{id}/eliminar")
    public String eliminarServicio(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        getBarberoSession(session);
        try {
            servicioService.eliminarDefinitivo(id);
            ra.addFlashAttribute("ok", "Servicio eliminado definitivamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/servicios";
    }

    @GetMapping("/horarios")
    public String horarios(HttpSession session, Model model) {
        Usuario barbero = getBarberoSession(session);
        model.addAttribute("horarios", horarioService.listarPorBarbero(barbero.getId()));
        model.addAttribute("excepciones", horarioService.listarExcepcionesPorBarbero(barbero.getId()));
        return "barbero/horarios";
    }

    @PostMapping("/horarios/guardar")
    public String guardarHorarios(@RequestParam("diaSemana") List<String> diasSemana,
                                  @RequestParam("libre") List<Boolean> libres,
                                  @RequestParam("horaInicio") List<String> horasInicio,
                                  @RequestParam("horaFin") List<String> horasFin,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Usuario barbero = getBarberoSession(session);

        List<HorarioGeneral> horarios = new ArrayList<>();
        for (int i = 0; i < diasSemana.size(); i++) {
            HorarioGeneral h = new HorarioGeneral();
            h.setDiaSemana(DayOfWeek.valueOf(diasSemana.get(i)));
            h.setLibre(libres.get(i));
            if (!h.isLibre()) {
                h.setHoraInicio(LocalTime.parse(horasInicio.get(i)));
                h.setHoraFin(LocalTime.parse(horasFin.get(i)));
            }
            horarios.add(h);
        }

        horarioService.guardarHorariosSemanales(barbero.getId(), horarios);
        ra.addFlashAttribute("ok", "Horario semanal guardado");
        return "redirect:/barbero/horarios";
    }

    @PostMapping("/excepciones/guardar")
    public String guardarExcepcion(@ModelAttribute ExcepcionHorario excepcion, HttpSession session, RedirectAttributes ra) {
        Usuario barbero = getBarberoSession(session);
        excepcion.setBarbero(barbero);
        try {
            List<Cita> afectadas = horarioService.obtenerCitasAfectadasPorExcepcion(barbero.getId(), excepcion);
            if (!afectadas.isEmpty()) {
                ra.addFlashAttribute("citasAfectadas", afectadas);
                ra.addFlashAttribute("excepcionGuardada", excepcion);
                return "redirect:/barbero/horarios/afectadas";
            }
            horarioService.guardarExcepcion(excepcion);
            ra.addFlashAttribute("ok", "Excepcion guardada");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/horarios";
    }

    @GetMapping("/horarios/afectadas")
    public String citasAfectadas(Model model) {
        if (!model.containsAttribute("citasAfectadas")) {
            return "redirect:/barbero/horarios";
        }
        return "barbero/citas-afectadas";
    }

    @PostMapping("/horarios/afectadas/reagendar")
    public String reagendarCitaAfectada(@RequestParam Long citaId,
                                        @RequestParam LocalDate nuevaFecha,
                                        @RequestParam LocalTime nuevaHoraInicio,
                                        RedirectAttributes ra) {
        try {
            citaService.reagendarCita(citaId, nuevaFecha, nuevaHoraInicio, "Modificacion de horario");
            ra.addFlashAttribute("ok", "Cita reagendada");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/horarios";
    }

    @PostMapping("/horarios/afectadas/cancelar")
    public String cancelarCitaAfectada(@RequestParam Long citaId, RedirectAttributes ra) {
        citaService.cancelarDirectamente(citaId, "Cancelada por modificacion de horario");
        ra.addFlashAttribute("ok", "Cita cancelada por modificacion de horario");
        return "redirect:/barbero/horarios";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCitaBarbero(HttpSession session, Model model) {
        Usuario barbero = getBarberoSession(session);
        List<HorarioGeneral> horarios = horarioService.listarPorBarbero(barbero.getId());
        List<ExcepcionHorario> excepciones = horarioService.listarExcepcionesFuturasPorBarbero(barbero.getId());

        List<Servicio> servicios = servicioService.listarPorBarbero(barbero.getId());
        model.addAttribute("clientes", usuarioService.listarClientesPorBarberia(barbero.getBarberia().getId()));
        model.addAttribute("servicios", servicios);
        model.addAttribute("barberoId", barbero.getId());
        model.addAttribute("barberiaId", barbero.getBarberia().getId());
        model.addAttribute("horarioSemanalJson", horarioGeneralToJson(horarios));
        model.addAttribute("excepcionesJson", excepcionesToJson(excepciones));
        model.addAttribute("serviciosJson", serviciosToJson(servicios));
        return "barbero/cita-form";
    }

    private String horarioGeneralToJson(List<HorarioGeneral> horarios) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < horarios.size(); i++) {
            HorarioGeneral h = horarios.get(i);
            sb.append("{");
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

    @PostMapping("/citas/guardar")
    public String guardarCitaBarbero(@RequestParam Long barberoId,
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
            ra.addFlashAttribute("ok", "Cita creada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/dashboard";
    }

    @GetMapping("/solicitudes")
    public String solicitudes(HttpSession session, Model model) {
        Usuario barbero = getBarberoSession(session);
        model.addAttribute("solicitudes", solicitudService.listarPendientesPorBarbero(barbero.getId()));
        return "barbero/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable Long id, RedirectAttributes ra) {
        try {
            solicitudService.aprobarSolicitud(id);
            ra.addFlashAttribute("ok", "Solicitud aprobada");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra) {
        solicitudService.rechazarSolicitud(id, motivo);
        ra.addFlashAttribute("ok", "Solicitud rechazada");
        return "redirect:/barbero/solicitudes";
    }
}
