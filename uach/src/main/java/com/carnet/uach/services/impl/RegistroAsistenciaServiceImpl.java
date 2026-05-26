package com.carnet.uach.services.impl;

import com.carnet.uach.models.Estudiante;
import com.carnet.uach.models.Evento;
import com.carnet.uach.models.RegistroAsistencia;
import com.carnet.uach.models.RegistroAsistenciaId;
import com.carnet.uach.repositories.RegistroAsistenciaRepository;
import com.carnet.uach.services.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {

    private final RegistroAsistenciaRepository registroAsistenciaRepository;
    private final String UPLOAD_DIR = "uploads/evidencias/";

    @Override
    public void guardarEvidencia(Long matricula, Long idEvento, String descripcion, MultipartFile archivo) throws IOException {
        // Crear carpeta de subidas si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre de archivo único para evitar colisiones
        String originalFilename = archivo.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Copiar el archivo físico a la carpeta
        Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Preparar la entidad para guardar en la BD
        RegistroAsistencia registro = new RegistroAsistencia();
        
        RegistroAsistenciaId id = new RegistroAsistenciaId();
        id.setIdEvento(idEvento);
        id.setMatricula(matricula);
        registro.setId(id);

        Evento evento = new Evento();
        evento.setIdEvento(idEvento);
        registro.setEvento(evento);

        Estudiante estudiante = new Estudiante();
        estudiante.setIdUsuario(matricula);
        registro.setEstudiante(estudiante);

        registro.setFechaRegistro(LocalDateTime.now());
        registro.setAsistenciaConfirmada(false); // Requiere aprobación del empleado en la Fase 4
        registro.setDescripcion(descripcion);
        registro.setEvidencia(filePath.toString()); // Se guarda la ruta física

        registroAsistenciaRepository.save(registro);
    }

    @Override
    public java.util.List<RegistroAsistencia> listarEvidenciasPendientes() {
        return registroAsistenciaRepository.findByAsistenciaConfirmadaFalse();
    }

    @Override
    public java.util.List<RegistroAsistencia> filtrarEvidenciasPendientes(Integer mes, String idCategoria) {
        return registroAsistenciaRepository.findByAsistenciaConfirmadaFalse().stream()
                .filter(r -> r.getEvento() != null)
                .filter(r -> mes == null || r.getEvento().getFechaFin() == null || r.getEvento().getFechaInicio().getMonthValue() == mes)
                .filter(r -> idCategoria == null || idCategoria.isEmpty() || 
                             (r.getEvento().getCategoria() != null && r.getEvento().getCategoria().getIdCategoria().equals(idCategoria)))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void aprobarEvidencia(Long matricula, Long idEvento) {
        RegistroAsistenciaId id = new RegistroAsistenciaId();
        id.setMatricula(matricula);
        id.setIdEvento(idEvento);

        RegistroAsistencia registro = registroAsistenciaRepository.findById(id).orElse(null);
        if (registro != null) {
            registro.setAsistenciaConfirmada(true);
            registroAsistenciaRepository.save(registro);
        }
    }

    @Override
    public void rechazarEvidencia(Long matricula, Long idEvento) {
        RegistroAsistenciaId id = new RegistroAsistenciaId();
        id.setMatricula(matricula);
        id.setIdEvento(idEvento);
        
        // Se elimina el registro para que el estudiante pueda volver a intentarlo
        registroAsistenciaRepository.deleteById(id);
    }

    @Override
    public java.util.List<RegistroAsistencia> obtenerRegistrosConfirmados(Long matricula) {
        return registroAsistenciaRepository.findByEstudiante_IdUsuarioAndAsistenciaConfirmadaTrue(matricula);
    }

    @Override
    public java.util.List<RegistroAsistencia> obtenerRegistrosPorEstudiante(Long matricula) {
        return registroAsistenciaRepository.findByEstudiante_IdUsuario(matricula);
    }

    @Override
    public RegistroAsistencia obtenerRegistroPorId(Long matricula, Long idEvento) {
        RegistroAsistenciaId id = new RegistroAsistenciaId();
        id.setMatricula(matricula);
        id.setIdEvento(idEvento);
        return registroAsistenciaRepository.findById(id).orElse(null);
    }
}
