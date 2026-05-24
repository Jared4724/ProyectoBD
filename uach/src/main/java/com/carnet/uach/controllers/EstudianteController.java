package com.carnet.uach.controllers;

import com.carnet.uach.services.EventoService;
import com.carnet.uach.services.RegistroAsistenciaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

    private final EventoService eventoService;
    private final RegistroAsistenciaService registroAsistenciaService;

    @GetMapping("/eventos")
    public String dashboardEstudiante(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/eventos";
    }

    @GetMapping("/subir-evidencia")
    public String subirEvidencia(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        // Se listan los eventos pasados para subir evidencia
        model.addAttribute("eventos", eventoService.listarEventosPasados());
        return "estudiante/subir-evidencia";
    }

    @PostMapping("/guardar-evidencia")
    public String guardarEvidencia(
            @RequestParam("id_evento") Long idEvento,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("evidencia_file") MultipartFile archivo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Long matricula = (Long) session.getAttribute("usuarioId");

        try {
            registroAsistenciaService.guardarEvidencia(matricula, idEvento, descripcion, archivo);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Evidencia subida correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al subir la evidencia: " + e.getMessage());
        }

        return "redirect:/estudiante/subir-evidencia";
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
