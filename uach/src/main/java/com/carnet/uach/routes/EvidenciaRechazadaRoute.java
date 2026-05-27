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
        // En Camel 4 con spring-rabbitmq el URI format es: 
        // spring-rabbitmq:exchangeName?queues=queueName&routingKey=routingKey&autoDeclare=false
        from("spring-rabbitmq:evidencias.exchange?queues=evidencias.rechazadas.queue&routingKey=evidencia.rechazada&autoDeclare=false")
            .routeId("evidencia-rechazada-route")
            .process(exchange -> {
                String rutaArchivo = exchange.getIn().getBody(String.class);
                if (rutaArchivo != null && !rutaArchivo.trim().isEmpty()) {
                    try {
                        Path path = Paths.get(rutaArchivo);
                        boolean borrado = Files.deleteIfExists(path);
                        if (borrado) {
                            logger.info("Evidencia eliminada físicamente del disco de forma asíncrona: {}", rutaArchivo);
                        } else {
                            logger.warn("El archivo no se encontró en el disco para borrar (posiblemente ya fue eliminado): {}", rutaArchivo);
                        }
                    } catch (Exception e) {
                        logger.error("Error al intentar borrar el archivo físico {}: {}", rutaArchivo, e.getMessage());
                        // Dependiendo de la política de reintentos, se podría relanzar la excepción.
                    }
                }
            });
    }
}
