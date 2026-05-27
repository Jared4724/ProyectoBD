package com.carnet.uach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
// Habilita el soporte general de caché en Spring (que usará Redis por la configuración de properties)
@EnableCaching
public class UachApplication {

	public static void main(String[] args) {
		SpringApplication.run(UachApplication.class, args);
	}

}
