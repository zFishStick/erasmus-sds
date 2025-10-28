package com.sds2;

import java.io.IOException;
import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;

@SpringBootApplication
public class Server {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Server.class, args);
        System.out.println("Server in esecuzione su http://localhost:8080");
        System.setProperty("java.awt.headless", "false");
        
        try {
            URI uri = new URI("http://localhost:8080");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            } else {
                System.out.println("Apertura automatica del browser non supportata.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

