package com.carnet.uach.controllers;

import com.carnet.uach.services.EventoService;
import com.carnet.uach.services.RegistroAsistenciaService;
import java.util.List;
import java.util.stream.Collectors;
import com.carnet.uach.models.Evento;
import com.carnet.uach.models.RegistroAsistencia;
import com.carnet.uach.repositories.CategoriaRepository;
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
    private final CategoriaRepository categoriaRepository;

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
                String cat = r.getEvento().getCategoria().getNombreCategoria().toUpperCase();
                int pts = r.getEvento().getPuntos();
                if (cat.contains("ARTISTICA") || cat.contains("CULTURAL")) puntosArtistica += pts;
                else if (cat.contains("CIENTIFICO") || cat.contains("FILOSOFICA")) puntosCientifica += pts;
                else if (cat.contains("DEPORTIV")) puntosDeportiva += pts;
                else if (cat.contains("HERRAMIENTAS")) puntosHerramientas += pts;
                else if (cat.contains("SALUD")) puntosSalud += pts;
                else if (cat.contains("COMUNIDAD")) puntosComunidad += pts;
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
        
        // Se listan los eventos cuya fecha límite aún no ha pasado
        List<com.carnet.uach.models.Evento> disponibles = eventoService.listarEventosDisponibles();
        
        // Filtramos los eventos para los que el alumno ya mandó evidencia (pendiente o aprobada)
        List<RegistroAsistencia> enviados = registroAsistenciaService.obtenerRegistrosPorEstudiante(matricula);
        disponibles.removeIf(evento -> enviados.stream().anyMatch(r -> r.getEvento().getIdEvento().equals(evento.getIdEvento())));
        
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
            redirectAttributes.addFlashAttribute("mensajeError", "Todos los campos son obligatorios (Descripción y Evidencia).");
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

    @GetMapping("/encuestas")
    public String encuestas(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "estudiante/encuestas";
    }

    @GetMapping("/proximos-eventos")
    public String proximosEventos(@RequestParam(required = false) Integer mes,
                                  @RequestParam(required = false) String idCategoria,
                                  @RequestParam(required = false) Boolean filter,
                                  HttpSession session, Model model) {
        // Si no hay filtro explícito y es la primera vez que entra, usamos el mes actual.
        if (mes == null && (filter == null || !filter)) {
            mes = java.time.LocalDate.now().getMonthValue();
        }
        
        List<Evento> todosDisponibles = eventoService.filtrarEventosDisponibles(mes, idCategoria);
        List<Evento> eventosNormales = todosDisponibles.stream()
                .filter(e -> e.getFechaFin() != null)
                .collect(Collectors.toList());
        List<Evento> eventosPermanentes = todosDisponibles.stream()
                .filter(e -> e.getFechaFin() == null)
                .collect(Collectors.toList());
        
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("eventos", eventosNormales);
        model.addAttribute("eventosPermanentes", eventosPermanentes);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("mesSeleccionado", mes);
        model.addAttribute("categoriaSeleccionada", idCategoria);
        return "estudiante/proximos-eventos";
    }
}
