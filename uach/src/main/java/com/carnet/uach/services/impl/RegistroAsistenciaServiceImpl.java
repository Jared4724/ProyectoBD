package com.carnet.uach.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.camel.ProducerTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.carnet.uach.models.Estudiante;
import com.carnet.uach.models.Evento;
import com.carnet.uach.models.RegistroAsistencia;
import com.carnet.uach.models.RegistroAsistenciaId;
import com.carnet.uach.repositories.RegistroAsistenciaRepository;
import com.carnet.uach.services.RegistroAsistenciaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {

    private final RegistroAsistenciaRepository registroAsistenciaRepository;
    
    // ========================================================================================
    // INTEGRACIÓN CON RABBITMQ (PRODUCTOR)
    // ========================================================================================
    // RabbitTemplate es la clase principal de Spring AMQP para enviar y recibir mensajes.
    // Aquí la inyectamos (gracias a @RequiredArgsConstructor) para poder publicar mensajes
    // hacia un exchange de RabbitMQ cuando se rechaza una evidencia.
    // ========================================================================================
    // ========================================================================================
    private final RabbitTemplate rabbitTemplate;
    
    // Inyectamos el ProducerTemplate de Camel para enviar mensajes a nuestras rutas SEDA
    private final ProducerTemplate producerTemplate;
    
    private final String UPLOAD_DIR = "../uploads/evidencias/";

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
        
        // Simular notificación al encargado usando Apache Camel (SEDA)
        producerTemplate.sendBody("seda:notificarEncargado", 
            "El estudiante con matrícula " + matricula + " ha subido una evidencia para el evento ID: " + idEvento);
    }

    @Override
    public java.util.List<RegistroAsistencia> listarEvidenciasPendientes() {
        return registroAsistenciaRepository.findByAsistenciaConfirmadaFalse();
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
            
            // Simular notificación al estudiante de APROBACIÓN usando Apache Camel (SEDA)
            producerTemplate.sendBody("seda:notificarEstudiante", 
                "Tu evidencia para el evento ID: " + idEvento + " ha sido APROBADA.");
        }
    }

    @Override
    public void rechazarEvidencia(Long matricula, Long idEvento) {
        RegistroAsistenciaId id = new RegistroAsistenciaId();
        id.setMatricula(matricula);
        id.setIdEvento(idEvento);
        
        RegistroAsistencia registro = registroAsistenciaRepository.findById(id).orElse(null);
        if (registro != null && registro.getEvidencia() != null) {
            String rutaEvidencia = registro.getEvidencia();
            
            // Se elimina el registro para que el estudiante pueda volver a intentarlo
            registroAsistenciaRepository.deleteById(id);
            
            // ========================================================================================
            // ENVÍO DE MENSAJE A RABBITMQ
            // ========================================================================================
            // Se publica un mensaje en RabbitMQ para indicar que se debe borrar el archivo físico.
            // Esto permite que la eliminación se haga de forma asíncrona mediante Apache Camel,
            // liberando el hilo actual y mejorando el tiempo de respuesta de la petición HTTP.
            // 
            // Parámetros:
            // 1. "evidencias.exchange": El nombre del Exchange en RabbitMQ.
            // 2. "evidencia.rechazada": El Routing Key que determinará a qué cola va el mensaje.
            // 3. rutaEvidencia: El cuerpo (payload) del mensaje, que es la ruta física del archivo.
            // ========================================================================================
            rabbitTemplate.convertAndSend("evidencias.exchange", "evidencia.rechazada", rutaEvidencia);
            
            // Simular notificación al estudiante de RECHAZO usando Apache Camel (SEDA)
            producerTemplate.sendBody("seda:notificarEstudiante", 
                "Lo sentimos. Tu evidencia para el evento ID: " + idEvento + " ha sido RECHAZADA.");
        } else {
            // Se elimina el registro directamente
            registroAsistenciaRepository.deleteById(id);
        }
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
