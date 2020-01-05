package com.bolsadeideas.springboot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class })
@SpringBootApplication
public class ULinkBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ULinkBackendApplication.class, args);
	}
	
	

}
