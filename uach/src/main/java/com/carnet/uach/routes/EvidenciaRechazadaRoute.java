package com.carnet.uach.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EvidenciaRechazadaRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(EvidenciaRechazadaRoute.class);

    @Override
    public void configure() throws Exception {
        // ========================================================================================
        // INTEGRACIÓN CAMEL + RABBITMQ (CONSUMIDOR)
        // ========================================================================================
        // Se utiliza el componente 'spring-rabbitmq' de Apache Camel para consumir mensajes 
        // de forma asíncrona desde un exchange de RabbitMQ.
        // 
        // Parámetros de la URI:
        // - exchangeName: 'evidencias.exchange' (El exchange al que nos conectamos).
        // - queues: 'evidencias.rechazadas.queue' (La cola de donde Camel leerá los mensajes).
        // - routingKey: 'evidencia.rechazada' (La clave de enrutamiento para filtrar mensajes).
        // - autoDeclare=false: Indica que las colas/exchanges ya están declaradas en la configuración.
        // ========================================================================================
        from("spring-rabbitmq:evidencias.exchange?queues=evidencias.rechazadas.queue&routingKey=evidencia.rechazada&autoDeclare=false")
            .routeId("evidencia-rechazada-route") // Identificador único de la ruta en Camel
            .process(exchange -> {
                // Aquí inicia el procesamiento del mensaje consumido.
                // El cuerpo del mensaje (Body) contiene la ruta del archivo a eliminar, 
                // que fue enviado como un String desde el servicio de RegistroAsistencia.
                String rutaArchivo = exchange.getIn().getBody(String.class);
                if (rutaArchivo != null && !rutaArchivo.trim().isEmpty()) {
                    try {
                        Path path = Paths.get(rutaArchivo);
                        // Se realiza la eliminación física del archivo
                        boolean borrado = Files.deleteIfExists(path);
                        if (borrado) {
                            logger.info("Evidencia eliminada físicamente del disco de forma asíncrona: {}", rutaArchivo);
                        } else {
                            logger.warn("El archivo no se encontró en el disco para borrar (posiblemente ya fue eliminado): {}", rutaArchivo);
                        }
                    } catch (Exception e) {
                        logger.error("Error al intentar borrar el archivo físico {}: {}", rutaArchivo, e.getMessage());
                        // Dependiendo de la política de reintentos de Camel/RabbitMQ, se podría relanzar la excepción.
                    }
                }
            });
    }
}
