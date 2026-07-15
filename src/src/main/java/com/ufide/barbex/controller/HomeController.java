package com.ufide.barbex.controller;

import com.ufide.barbex.entity.Rol;
import com.ufide.barbex.entity.Usuario;
import com.ufide.barbex.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String home(HttpSession session) {
        Usuario usuario = usuarioService.getUsuarioSession(session);
        if (usuario == null) {
            return "redirect:/login";
        }
        if (usuario.getRol() == Rol.BARBERO) {
            return "redirect:/barbero/dashboard";
        }
        return "redirect:/cliente/dashboard";
    }
}
