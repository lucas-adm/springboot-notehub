package br.com.notehub;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@EnableScheduling
@SpringBootApplication
public class NoteHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoteHubApplication.class, args);
    }

}