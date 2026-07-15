package com.ufide.barbex.controller;

import com.ufide.barbex.entity.Rol;
import com.ufide.barbex.entity.Usuario;
import com.ufide.barbex.service.BarberiaService;
import com.ufide.barbex.service.BarberiaService;
import com.ufide.barbex.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BarberiaService barberiaService;

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {
        if (!usuarioService.validarLogin(email, password)) {
            ra.addFlashAttribute("error", "Email o contrasena incorrectos");
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.findByEmail(email).orElseThrow();
        usuarioService.login(session, usuario);
        return "redirect:/";
    }

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
                           RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("barberias", barberiaService.listarTodas());
            return "auth/registro";
        }

        if (usuario.getRol() == Rol.CLIENTE && (barberiaId == null || barberiaId <= 0)) {
            model.addAttribute("error", "El cliente debe seleccionar una barberia existente");
            model.addAttribute("roles", Rol.values());
            model.addAttribute("barberias", barberiaService.listarTodas());
            return "auth/registro";
        }

        if (telefonoBarberia != null && !telefonoBarberia.isBlank() && !telefonoBarberia.matches("^[0-9\\-]+$")) {
            model.addAttribute("error", "El telefono de la barberia solo puede contener numeros y guiones");
            model.addAttribute("roles", Rol.values());
            model.addAttribute("barberias", barberiaService.listarTodas());
            return "auth/registro";
        }

        try {
            usuarioService.registrar(usuario, nombreBarberia, direccionBarberia, telefonoBarberia, barberiaId);
            ra.addFlashAttribute("ok", "Cuenta creada correctamente");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", Rol.values());
            return "auth/registro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        usuarioService.logout(session);
        return "redirect:/login";
    }
}
