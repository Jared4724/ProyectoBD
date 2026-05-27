package com.carnet.uach.config;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class LimpiezaArchivosRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // CAMEL + RABBITMQ (RUTA 1):
        // Este puente escucha peticiones internas de la aplicación (usando "direct:borrarArchivo").
        // Cuando recibe la ruta de un archivo, se la manda a RabbitMQ a través de un intercambio (Exchange) directo.
        from("direct:borrarArchivo")
                .to("spring-rabbitmq:amq.direct?routingKey=evidencias.rechazadas");

        // CAMEL + RABBITMQ (RUTA 2 - BACKGROUND WORKER):
        // Esta ruta está "escuchando" silenciosamente la cola de RabbitMQ.
        // Cuando RabbitMQ recibe el mensaje, Camel lo saca de la cola y ejecuta el método
        // "borrarArchivoFisico" de nuestro ArchivoService en segundo plano.
        from("spring-rabbitmq:amq.direct?queues=q.uach.limpieza_archivos&routingKey=evidencias.rechazadas&autoDeclare=true")
                .bean("archivoService", "borrarArchivoFisico");
    }
}
