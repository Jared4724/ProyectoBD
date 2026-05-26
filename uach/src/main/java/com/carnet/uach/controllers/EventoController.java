package com.carnet.uach.controllers;

import com.carnet.uach.models.Evento;
import com.carnet.uach.repositories.CategoriaRepository;
import com.carnet.uach.services.EventoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/empleado/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final CategoriaRepository categoriaRepository;

    /**
     * Muestra la lista de todos los eventos.
     */
    @GetMapping({"", "/"})
    public String listarEventos(Model model) {
        java.util.List<Evento> eventos = eventoService.listarTodos();
        
        model.addAttribute("eventos", eventos);
        
        return "empleado/eventos";
    }

    /**
     * Muestra el formulario para crear un nuevo evento.
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "empleado/evento-form";
    }

    /**
     * Guarda el evento. Se usa @ModelAttribute para recibir los datos del formulario.
     * Se extrae el ID del usuario de la sesión para asignarlo como organizador.
     */
    @PostMapping("/guardar")
    public String guardarEvento(@ModelAttribute Evento evento, HttpSession session) {
        // Extrae el ID del usuario (empleado) desde la sesión
        Long idEmpleadoOrganizador = (Long) session.getAttribute("usuarioId");
        
        // Llama al servicio para guardar
        eventoService.guardarEvento(evento, idEmpleadoOrganizador);
        
        return "redirect:/empleado/eventos";
    }

}
