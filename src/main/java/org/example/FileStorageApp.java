package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FileStorageApp {
    public static void main(String[] args) {
        SpringApplication.run(FileStorageApp.class, args);
    }
}
