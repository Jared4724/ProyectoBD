package com.carnet.uach.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_EVIDENCIAS = "evidencias.rechazadas.queue";
    public static final String EXCHANGE_EVIDENCIAS = "evidencias.exchange";
    public static final String ROUTING_KEY_EVIDENCIAS = "evidencia.rechazada";

    @Bean
    public Queue evidenciasQueue() {
        return new Queue(QUEUE_EVIDENCIAS, true); // durable
    }

    @Bean
    public DirectExchange evidenciasExchange() {
        return new DirectExchange(EXCHANGE_EVIDENCIAS);
    }

    @Bean
    public Binding bindingEvidencias(Queue evidenciasQueue, DirectExchange evidenciasExchange) {
        return BindingBuilder.bind(evidenciasQueue).to(evidenciasExchange).with(ROUTING_KEY_EVIDENCIAS);
    }
}
