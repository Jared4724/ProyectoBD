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
}
