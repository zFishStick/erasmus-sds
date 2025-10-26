package com.sds2;

import com.amadeus.exceptions.ResponseException;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {
    public static void main(String[] args) throws IOException, ResponseException, InterruptedException {
        SpringApplication.run(Server.class, args);
        System.out.println("Server in esecuzione su http://localhost:8080");
    }

}
