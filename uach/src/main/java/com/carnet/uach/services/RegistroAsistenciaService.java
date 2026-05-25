package com.carnet.uach.services;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface RegistroAsistenciaService {
    
    /**
     * Guarda la evidencia subida por el estudiante para un evento específico.
     * 
     * @param matricula ID del estudiante.
     * @param idEvento ID del evento.
     * @param descripcion Descripción de la experiencia.
     * @param archivo Archivo Multipart de la imagen.
     * @throws IOException Si hay un error guardando el archivo.
     */
    void guardarEvidencia(Long matricula, Long idEvento, String descripcion, MultipartFile archivo) throws IOException;

    java.util.List<com.carnet.uach.models.RegistroAsistencia> listarEvidenciasPendientes();

    void aprobarEvidencia(Long matricula, Long idEvento);

    void rechazarEvidencia(Long matricula, Long idEvento);

    java.util.List<com.carnet.uach.models.RegistroAsistencia> obtenerRegistrosConfirmados(Long matricula);

    java.util.List<com.carnet.uach.models.RegistroAsistencia> obtenerRegistrosPorEstudiante(Long matricula);

    com.carnet.uach.models.RegistroAsistencia obtenerRegistroPorId(Long matricula, Long idEvento);
}
