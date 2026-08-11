package com.ufide.barbex.controller;

import com.ufide.barbex.entity.*;
import com.ufide.barbex.security.CustomUserDetails;
import com.ufide.barbex.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/barbero")
@PreAuthorize("hasRole('BARBERO')")
public class BarberoController {

    @Autowired
    private MessageSource messageSource;

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

    private Usuario getBarbero(CustomUserDetails userDetails) {
        return userDetails.getUsuario();
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario barbero = getBarbero(userDetails);
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
    public String completarCita(@PathVariable Long id, RedirectAttributes ra, Locale locale) {
        citaService.marcarCompletada(id);
        ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.cita.completada", null, locale));
        return "redirect:/barbero/dashboard";
    }

    @PostMapping("/citas/{id}/cancelar")
    public String cancelarCita(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra, Locale locale) {
        citaService.cancelarDirectamente(id, motivo);
        ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.cita.cancelada", null, locale));
        return "redirect:/barbero/dashboard";
    }

    @PostMapping("/citas/{id}/aprobar-adelanto")
    public String aprobarAdelanto(@PathVariable Long id, RedirectAttributes ra, Locale locale) {
        try {
            citaService.aprobarAdelanto(id);
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.adelanto.aprobado", null, locale));
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/dashboard";
    }

    @PostMapping("/citas/{id}/rechazar")
    public String rechazarCita(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra, Locale locale) {
        citaService.rechazarCita(id, motivo);
        ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.cita.rechazada", null, locale));
        return "redirect:/barbero/dashboard";
    }

    @GetMapping("/citas/{id}/editar")
    public String editarCita(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario barbero = getBarbero(userDetails);
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
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes ra,
                                 Locale locale) {
        Usuario barbero = getBarbero(userDetails);
        Cita cita = citaService.findById(id).orElseThrow();
        if (!cita.getBarbero().getId().equals(barbero.getId())) {
            return "redirect:/barbero/dashboard";
        }
        try {
            citaService.editarCita(id, nuevaFecha, nuevaHoraInicio);
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.cita.actualizada", null, locale));
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/dashboard";
    }

    @GetMapping("/servicios")
    public String servicios(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario barbero = getBarbero(userDetails);
        model.addAttribute("servicios", servicioService.listarPorBarberia(barbero.getBarberia().getId()));
        model.addAttribute("barbero", barbero);
        return "barbero/servicios";
    }

    @GetMapping("/servicios/nuevo")
    public String nuevoServicio(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Servicio servicio = new Servicio();
        servicio.setBarberia(getBarbero(userDetails).getBarberia());
        model.addAttribute("servicio", servicio);
        return "barbero/servicio-form";
    }

    @PostMapping("/servicios/guardar")
    public String guardarServicio(@ModelAttribute Servicio servicio, @AuthenticationPrincipal CustomUserDetails userDetails, Model model, RedirectAttributes ra, Locale locale) {
        servicio.setBarberia(getBarbero(userDetails).getBarberia());
        try {
            servicioService.guardar(servicio);
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.servicio.guardado", null, locale));
            return "redirect:/barbero/servicios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("servicio", servicio);
            return "barbero/servicio-form";
        }
    }

    @PostMapping("/servicios/{id}/eliminar")
    public String eliminarServicio(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes ra, Locale locale) {
        getBarbero(userDetails);
        try {
            servicioService.eliminarDefinitivo(id);
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.servicio.eliminado", null, locale));
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/servicios";
    }

    @GetMapping("/horarios")
    public String horarios(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario barbero = getBarbero(userDetails);
        model.addAttribute("horarios", horarioService.listarPorBarbero(barbero.getId()));
        model.addAttribute("excepciones", horarioService.listarExcepcionesPorBarbero(barbero.getId()));
        return "barbero/horarios";
    }

    @PostMapping("/horarios/guardar")
    public String guardarHorarios(@RequestParam("diaSemana") List<String> diasSemana,
                                  @RequestParam("libre") List<Boolean> libres,
                                  @RequestParam("horaInicio") List<String> horasInicio,
                                  @RequestParam("horaFin") List<String> horasFin,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  RedirectAttributes ra,
                                  Locale locale) {
        Usuario barbero = getBarbero(userDetails);

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
        ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.horario.guardado", null, locale));
        return "redirect:/barbero/horarios";
    }

    @PostMapping("/excepciones/guardar")
    public String guardarExcepcion(@ModelAttribute ExcepcionHorario excepcion, @AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes ra, Locale locale) {
        Usuario barbero = getBarbero(userDetails);
        excepcion.setBarbero(barbero);
        try {
            List<Cita> afectadas = horarioService.obtenerCitasAfectadasPorExcepcion(barbero.getId(), excepcion);
            if (!afectadas.isEmpty()) {
                ra.addFlashAttribute("citasAfectadas", afectadas);
                ra.addFlashAttribute("excepcionGuardada", excepcion);
                return "redirect:/barbero/horarios/afectadas";
            }
            horarioService.guardarExcepcion(excepcion);
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.excepcion.guardada", null, locale));
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
                                        RedirectAttributes ra,
                                        Locale locale) {
        try {
            citaService.reagendarCita(citaId, nuevaFecha, nuevaHoraInicio, "Modificacion de horario");
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.cita.reagendada", null, locale));
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/horarios";
    }

    @PostMapping("/horarios/afectadas/cancelar")
    public String cancelarCitaAfectada(@RequestParam Long citaId, RedirectAttributes ra, Locale locale) {
        citaService.cancelarDirectamente(citaId, "Cancelada por modificacion de horario");
        ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.cita.canceladaHorario", null, locale));
        return "redirect:/barbero/horarios";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCitaBarbero(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario barbero = getBarbero(userDetails);
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
                                     RedirectAttributes ra,
                                     Locale locale) {
        try {
            citaService.crearCita(barberoId, clienteId, barberiaId, fecha, horaInicio, servicios, adelantoPagado, comprobanteAdelanto);
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.cita.creada", null, locale));
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/dashboard";
    }

    @GetMapping("/solicitudes")
    public String solicitudes(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario barbero = getBarbero(userDetails);
        model.addAttribute("solicitudes", solicitudService.listarPendientesPorBarbero(barbero.getId()));
        return "barbero/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable Long id, RedirectAttributes ra, Locale locale) {
        try {
            solicitudService.aprobarSolicitud(id);
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.solicitud.aprobada", null, locale));
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/barbero/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable Long id, @RequestParam String motivo, RedirectAttributes ra, Locale locale) {
        solicitudService.rechazarSolicitud(id, motivo);
        ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.solicitud.rechazada", null, locale));
        return "redirect:/barbero/solicitudes";
    }
}
