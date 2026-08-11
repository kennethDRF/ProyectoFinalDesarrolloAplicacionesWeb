package com.ufide.barbex.controller;

import com.ufide.barbex.entity.Rol;
import com.ufide.barbex.entity.Usuario;
import com.ufide.barbex.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        if (usuario.getRol() == Rol.BARBERO) {
            return "redirect:/barbero/dashboard";
        }
        return "redirect:/cliente/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/403")
    public String accesoDenegado() {
        return "403";
    }
}
