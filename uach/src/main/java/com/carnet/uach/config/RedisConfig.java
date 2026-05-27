package com.carnet.uach.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
// Esta anotación intercepta todas las sesiones HTTP de los usuarios y, 
// en lugar de guardarlas en la memoria temporal del servidor Tomcat, 
// las guarda permanentemente en la base de datos en memoria Redis.
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600) // 1 hora de sesión
public class RedisConfig {
}
