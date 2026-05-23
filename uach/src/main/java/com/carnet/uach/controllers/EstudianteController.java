package com.carnet.uach.controllers;

import com.carnet.uach.services.EventoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

    private final EventoService eventoService;

    @GetMapping("/eventos")
    public String dashboardEstudiante(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/eventos";
    }

    @GetMapping("/subir-evidencia")
    public String subirEvidencia(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/subir-evidencia";
    }

    @GetMapping("/encuestas")
    public String encuestas(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/encuestas";
    }

    @GetMapping("/proximos-eventos")
    public String proximosEventos(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("eventos", eventoService.listarEventosDisponibles());
        return "estudiante/proximos-eventos";
    }
}
