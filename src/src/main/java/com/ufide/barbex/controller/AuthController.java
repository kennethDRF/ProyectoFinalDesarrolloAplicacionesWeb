package com.ufide.barbex.controller;

import com.ufide.barbex.entity.Rol;
import com.ufide.barbex.entity.Usuario;
import com.ufide.barbex.service.BarberiaService;
import com.ufide.barbex.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class AuthController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BarberiaService barberiaService;

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("barberias", barberiaService.listarTodas());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registro(@Valid @ModelAttribute("usuario") Usuario usuario,
                           BindingResult result,
                           @RequestParam(required = false) String nombreBarberia,
                           @RequestParam(required = false) String direccionBarberia,
                           @RequestParam(required = false) String telefonoBarberia,
                           @RequestParam(required = false) Long barberiaId,
                           Model model,
                           RedirectAttributes ra,
                           Locale locale) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("barberias", barberiaService.listarTodas());
            return "auth/registro";
        }

        if (usuario.getRol() == Rol.CLIENTE && (barberiaId == null || barberiaId <= 0)) {
            model.addAttribute("error", messageSource.getMessage("registro.error.clienteBarberia", null, locale));
            model.addAttribute("roles", Rol.values());
            model.addAttribute("barberias", barberiaService.listarTodas());
            return "auth/registro";
        }

        if (telefonoBarberia != null && !telefonoBarberia.isBlank() && !telefonoBarberia.matches("^[0-9\\-]+$")) {
            model.addAttribute("error", messageSource.getMessage("registro.error.telefono", null, locale));
            model.addAttribute("roles", Rol.values());
            model.addAttribute("barberias", barberiaService.listarTodas());
            return "auth/registro";
        }

        try {
            usuarioService.registrar(usuario, nombreBarberia, direccionBarberia, telefonoBarberia, barberiaId);
            ra.addFlashAttribute("ok", messageSource.getMessage("mensaje.cuenta.creada", null, locale));
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", Rol.values());
            return "auth/registro";
        }
    }
}
