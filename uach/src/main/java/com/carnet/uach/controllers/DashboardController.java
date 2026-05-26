package com.carnet.uach.controllers;

import com.carnet.uach.services.RegistroAsistenciaService;
import com.carnet.uach.repositories.CategoriaRepository;
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
    private final CategoriaRepository categoriaRepository;

    @GetMapping("/dashboard/empleado")
    public String dashboardEmpleado(@RequestParam(required = false) Integer mes,
                                    @RequestParam(required = false) String idCategoria,
                                    HttpSession session, Model model) {
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        
        java.util.List<com.carnet.uach.models.RegistroAsistencia> evidencias = registroAsistenciaService.listarEvidenciasPendientes();
        if (mes != null || (idCategoria != null && !idCategoria.isEmpty())) {
            evidencias = registroAsistenciaService.filtrarEvidenciasPendientes(mes, idCategoria);
        }
        
        model.addAttribute("evidencias", evidencias);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("mesSeleccionado", mes);
        model.addAttribute("categoriaSeleccionada", idCategoria);
        
        return "empleado/dashboard";
    }

    @PostMapping("/empleado/aprobar-evidencia")
    public String aprobarEvidencia(@RequestParam("matricula") Long matricula,
            @RequestParam("id_evento") Long idEvento) {
        registroAsistenciaService.aprobarEvidencia(matricula, idEvento);
        return "redirect:/dashboard/empleado";
    }

    @PostMapping("/empleado/rechazar-evidencia")
    public String rechazarEvidencia(@RequestParam("matricula") Long matricula,
            @RequestParam("id_evento") Long idEvento) {
        registroAsistenciaService.rechazarEvidencia(matricula, idEvento);
        return "redirect:/dashboard/empleado";
    }

    @GetMapping("/empleado/ver-foto")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.Map<String, Object> verFoto(
            @RequestParam("matricula") Long matricula,
            @RequestParam("id_evento") Long idEvento) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        com.carnet.uach.models.RegistroAsistencia registro = registroAsistenciaService.obtenerRegistroPorId(matricula,
                idEvento);

        if (registro == null || registro.getEvidencia() == null || registro.getEvidencia().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Sin evidencia");
            return response;
        }

        java.nio.file.Path path = java.nio.file.Paths.get(registro.getEvidencia());
        if (!java.nio.file.Files.exists(path)) {
            response.put("success", false);
            response.put("message", "Sin evidencia");
            return response;
        }

        // Si existe, devolver la ruta real
        String urlRuta = "/" + registro.getEvidencia().replace("\\", "/");
        response.put("success", true);
        response.put("url", urlRuta);
        return response;
    }
}
