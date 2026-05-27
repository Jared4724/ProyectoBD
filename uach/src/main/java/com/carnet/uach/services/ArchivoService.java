package com.carnet.uach.services;

import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("archivoService")
public class ArchivoService {
    
    // CAMEL + RABBITMQ WORKER:
    // Este método nunca es llamado directamente por tu código web ni bloquea la pantalla del usuario.
    // Es invocado automáticamente por Apache Camel en un hilo secundario cuando RabbitMQ entrega un mensaje.
    public void borrarArchivoFisico(String rutaArchivo) {
        try {
            Path path = Paths.get(rutaArchivo);
            boolean borrado = Files.deleteIfExists(path);
            if (borrado) {
                System.out.println("🗑️ [RABBITMQ ASYNC] Archivo de evidencia eliminado físicamente: " + rutaArchivo);
            } else {
                System.out.println("⚠️ [RABBITMQ ASYNC] El archivo no se encontró: " + rutaArchivo);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al intentar borrar el archivo: " + e.getMessage());
        }
    }
}
