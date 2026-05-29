package com.carnet.uach.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificacionRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionRoute.class);

    @Override
    public void configure() throws Exception {
        // ========================================================================================
        // RUTAS DE SIMULACIÓN DE NOTIFICACIONES (USANDO SEDA)
        // SEDA es un componente de Camel que crea colas asíncronas en la memoria de la aplicación.
        // Es perfecto para simulaciones rápidas sin depender de RabbitMQ.
        // ========================================================================================

        // Ruta 1: Notificar al estudiante cuando se acepta/rechaza su evidencia
        from("seda:notificarEstudiante")
            .routeId("notificar-estudiante-route")
            .process(exchange -> {
                String mensaje = exchange.getIn().getBody(String.class);
                // Aquí simularíamos el envío de un correo electrónico real usando camel-mail
                logger.info("==================================================");
                logger.info("📧 [NOTIFICACIÓN ENVIADA AL ESTUDIANTE]");
                logger.info("Mensaje: {}", mensaje);
                logger.info("==================================================");
            });

        // Ruta 2: Notificar al encargado (empleado) cuando un estudiante sube una evidencia
        from("seda:notificarEncargado")
            .routeId("notificar-encargado-route")
            .process(exchange -> {
                String mensaje = exchange.getIn().getBody(String.class);
                // Aquí simularíamos enviar un mensaje al dashboard del empleado o un correo
                logger.info("==================================================");
                logger.info("🔔 [ALERTA PARA EL ENCARGADO]");
                logger.info("Mensaje: {}", mensaje);
                logger.info("==================================================");
            });
    }
}
