package com.carnet.uach.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/estudiante/eventos")
    public String dashboardEstudiante(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/eventos";
    }

    @GetMapping("/dashboard/empleado")
    public String dashboardEmpleado(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "empleado/dashboard";
    }

    @GetMapping("/estudiante/subir-evidencia")
    public String subirEvidencia(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/subir-evidencia";
    }

    @GetMapping("/estudiante/encuestas")
    public String encuestas(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/encuestas";
    }

    @GetMapping("/estudiante/proximos-eventos")
    public String proximosEventos(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/proximos-eventos";
    }
}
