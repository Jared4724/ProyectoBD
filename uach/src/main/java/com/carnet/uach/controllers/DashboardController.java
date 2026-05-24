package com.carnet.uach.controllers;

import com.carnet.uach.services.RegistroAsistenciaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final RegistroAsistenciaService registroAsistenciaService;

    @GetMapping("/dashboard/empleado")
    public String dashboardEmpleado(HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("evidencias", registroAsistenciaService.listarEvidenciasPendientes());
        return "empleado/dashboard";
    }

    @PostMapping("/empleado/aprobar-evidencia")
    public String aprobarEvidencia(@RequestParam("matricula") Long matricula, @RequestParam("id_evento") Long idEvento) {
        registroAsistenciaService.aprobarEvidencia(matricula, idEvento);
        return "redirect:/dashboard/empleado";
    }

    @PostMapping("/empleado/rechazar-evidencia")
    public String rechazarEvidencia(@RequestParam("matricula") Long matricula, @RequestParam("id_evento") Long idEvento) {
        registroAsistenciaService.rechazarEvidencia(matricula, idEvento);
        return "redirect:/dashboard/empleado";
    }
}
