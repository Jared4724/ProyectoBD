package com.carnet.uach.controllers;

import com.carnet.uach.models.Empleado;
import com.carnet.uach.models.Estudiante;
import com.carnet.uach.models.Usuario;
import com.carnet.uach.services.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping({"/", "/login"})
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("usuarioId") != null) {
            String rol = (String) session.getAttribute("rol");
            if ("ESTUDIANTE".equals(rol)) return "redirect:/estudiante/eventos";
            if ("EMPLEADO".equals(rol)) return "redirect:/empleado/validaciones";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String correo, 
                        @RequestParam String contrasena, 
                        HttpSession session, 
                        Model model) {
        
        Usuario usuario = authService.autenticarUsuario(correo, contrasena);

        if (usuario != null) {
            // Guardar información básica en la sesión
            session.setAttribute("usuarioId", usuario.getIdUsuario());
            session.setAttribute("usuarioNombre", usuario.getNombre() + " " + usuario.getApellido());
            session.setAttribute("usuarioCorreo", usuario.getCorreo());

            // Redirigir según el tipo de usuario
            if (usuario instanceof Estudiante) {
                session.setAttribute("rol", "ESTUDIANTE");
                return "redirect:/estudiante/eventos";
            } else if (usuario instanceof Empleado) {
                session.setAttribute("rol", "EMPLEADO");
                return "redirect:/empleado/validaciones";
            }
        }

        // Si falla la autenticación
        model.addAttribute("error", "Credenciales inválidas");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
