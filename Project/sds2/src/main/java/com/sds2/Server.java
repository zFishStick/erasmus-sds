package com.sds2;

import java.awt.Desktop;
import java.net.URI;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {
    public static final String LOCALHOST = "http://localhost:8080";

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
        System.setProperty("java.awt.headless", "false");            
        try {
            URI uri = new URI(LOCALHOST);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            } else {
                Logger.getLogger(Server.class.getName()).severe("Desktop is not supported. Please open " + LOCALHOST + " manually.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.getLogger(Server.class.getName()).info("Server executing at " + LOCALHOST);
    }
}

