package com.ufide.barbex.service;

import com.ufide.barbex.entity.*;
import com.ufide.barbex.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    public static final String USUARIO_SESSION = "usuario";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BarberiaRepository barberiaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private BarberoServicioRepository barberoServicioRepository;

    @Autowired
    private HorarioService horarioService;

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario getUsuarioSession(HttpSession session) {
        return (Usuario) session.getAttribute(USUARIO_SESSION);
    }

    public boolean isLoggedIn(HttpSession session) {
        return getUsuarioSession(session) != null;
    }

    public void login(HttpSession session, Usuario usuario) {
        session.setAttribute(USUARIO_SESSION, usuario);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public boolean validarLogin(String email, String password) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        return opt.isPresent() && opt.get().getPassword().equals(password);
    }

    @Transactional
    public Usuario registrar(Usuario usuario, String nombreBarberia, String direccionBarberia,
                             String telefonoBarberia, Long barberiaId) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya esta registrado");
        }

        Barberia barberia = obtenerOCrearBarberia(nombreBarberia, direccionBarberia, telefonoBarberia, barberiaId);

        usuario.setBarberia(barberia);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        // Si es barbero, asignarle todos los servicios activos de la barberia por defecto
        if (usuario.getRol() == Rol.BARBERO) {
            List<Servicio> servicios = servicioRepository.findByBarberiaIdAndActivoTrue(barberia.getId());
            for (Servicio servicio : servicios) {
                barberoServicioRepository.save(new BarberoServicio(usuario, servicio));
            }
            // Crear horario por defecto: todos los dias libres
            horarioService.crearHorarioPorDefecto(usuario.getId());
        }

        return usuario;
    }

    private Barberia obtenerOCrearBarberia(String nombreBarberia, String direccionBarberia,
                                           String telefonoBarberia, Long barberiaId) {
        if (barberiaId != null) {
            return barberiaRepository.findById(barberiaId)
                    .orElseThrow(() -> new RuntimeException("Barberia no encontrada"));
        }

        if (nombreBarberia == null || nombreBarberia.isBlank()) {
            throw new RuntimeException("Debe seleccionar una barberia existente o crear una nueva");
        }

        Barberia barberia = new Barberia();
        barberia.setNombre(nombreBarberia);
        barberia.setDireccion(direccionBarberia);
        barberia.setTelefono(telefonoBarberia);
        barberia.setFechaRegistro(LocalDateTime.now());
        barberia.setCodigoAcceso("BAR" + System.currentTimeMillis());
        return barberiaRepository.save(barberia);
    }

    public List<Usuario> listarPorBarberia(Long barberiaId) {
        return usuarioRepository.findByBarberiaId(barberiaId);
    }

    public List<Usuario> listarBarberosPorBarberia(Long barberiaId) {
        return usuarioRepository.findByBarberiaId(barberiaId).stream()
                .filter(u -> u.getRol() == Rol.BARBERO)
                .toList();
    }

    public List<Usuario> listarClientesPorBarberia(Long barberiaId) {
        return usuarioRepository.findByBarberiaId(barberiaId).stream()
                .filter(u -> u.getRol() == Rol.CLIENTE)
                .toList();
    }
}
