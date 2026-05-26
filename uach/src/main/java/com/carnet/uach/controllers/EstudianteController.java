package com.carnet.uach.controllers;

import com.carnet.uach.services.EventoService;
import com.carnet.uach.services.RegistroAsistenciaService;
import java.util.List;
import java.util.stream.Collectors;
import com.carnet.uach.models.Evento;
import com.carnet.uach.models.RegistroAsistencia;
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
        Long matricula = (Long) session.getAttribute("usuarioId");
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));

        List<RegistroAsistencia> confirmados = registroAsistenciaService.obtenerRegistrosConfirmados(matricula);

        int puntosArtistica = 0;
        int puntosCientifica = 0;
        int puntosDeportiva = 0;
        int puntosHerramientas = 0;
        int puntosSalud = 0;
        int puntosComunidad = 0;

        for (RegistroAsistencia r : confirmados) {
            if (r.getEvento() != null && r.getEvento().getCategoria() != null) {
                String idCat = r.getEvento().getCategoria().getIdCategoria().toUpperCase();
                int pts = r.getEvento().getPuntos() != null ? r.getEvento().getPuntos() : 0;

                if (idCat.equals("CULT"))
                    puntosArtistica += pts;
                else if (idCat.equals("CIEN"))
                    puntosCientifica += pts;
                else if (idCat.equals("DEP"))
                    puntosDeportiva += pts;
                else if (idCat.equals("HERR"))
                    puntosHerramientas += pts;
                else if (idCat.equals("SAL"))
                    puntosSalud += pts;
                else if (idCat.equals("COM"))
                    puntosComunidad += pts;
            }
        }

        model.addAttribute("puntosArtistica", Math.min(puntosArtistica, 6));
        model.addAttribute("puntosCientifica", Math.min(puntosCientifica, 6));
        model.addAttribute("puntosDeportiva", Math.min(puntosDeportiva, 6));
        model.addAttribute("puntosHerramientas", Math.min(puntosHerramientas, 8));
        model.addAttribute("puntosSalud", Math.min(puntosSalud, 6));
        model.addAttribute("puntosComunidad", Math.min(puntosComunidad, 6));
        model.addAttribute("historial", confirmados);

        return "estudiante/eventos";
    }

    @GetMapping("/subir-evidencia")
    public String subirEvidencia(HttpSession session, Model model) {
        Long matricula = (Long) session.getAttribute("usuarioId");
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));

        List<com.carnet.uach.models.Evento> disponibles = new java.util.ArrayList<>(
                eventoService.listarEventosDisponibles());

        List<RegistroAsistencia> enviados = registroAsistenciaService.obtenerRegistrosPorEstudiante(matricula);
        disponibles.removeIf(
                evento -> enviados.stream().anyMatch(r -> r.getEvento().getIdEvento().equals(evento.getIdEvento())));

        model.addAttribute("eventos", disponibles);
        return "estudiante/subir-evidencia";
    }

    @PostMapping("/guardar-evidencia")
    public String guardarEvidencia(
            @RequestParam("id_evento") Long idEvento,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("evidencia_file") MultipartFile archivo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (descripcion == null || descripcion.trim().isEmpty() || archivo == null || archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Todos los campos son obligatorios (Descripción y Evidencia).");
            return "redirect:/estudiante/subir-evidencia";
        }

        Long matricula = (Long) session.getAttribute("usuarioId");

        try {
            registroAsistenciaService.guardarEvidencia(matricula, idEvento, descripcion, archivo);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Evidencia subida correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al subir la evidencia: " + e.getMessage());
        }

        return "redirect:/estudiante/subir-evidencia";
    }

    @GetMapping("/proximos-eventos")
    public String proximosEventos(HttpSession session, Model model) {
        List<Evento> todosDisponibles = eventoService.listarEventosDisponibles();
        List<Evento> eventosNormales = todosDisponibles.stream()
                .filter(e -> e.getFechaFin() != null)
                .collect(Collectors.toList());
        List<Evento> eventosPermanentes = todosDisponibles.stream()
                .filter(e -> e.getFechaFin() == null)
                .collect(Collectors.toList());

        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("eventos", eventosNormales);
        model.addAttribute("eventosPermanentes", eventosPermanentes);
        return "estudiante/proximos-eventos";
    }
}
