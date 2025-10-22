package com.sds2;


import com.sun.net.httpserver.HttpServer;

import com.sun.net.httpserver.HttpHandler;
import com.amadeus.exceptions.ResponseException;
import com.sds2.api.AmadeusAPI;
import com.sds2.classes.InputData;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Server {
    public static void main(String[] args) throws IOException, ResponseException {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("Project/.env"))) {
            props.load(input);
            AmadeusAPI amadeusAPI = new AmadeusAPI(
                props.getProperty("AMADEUS_API_KEY"),
                props.getProperty("AMADEUS_API_SECRET")
            );
            amadeusAPI.registerAmadeusEndpoints(server);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Retrieve Mapbox API key from properties file and serve it via an endpoint
        server.createContext("/api-key", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String apiKey = props.getProperty("MAPBOX_API_KEY");
                String response = "{\"key\": \"" + apiKey + "\"}";
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        
        server.createContext("/travel-info", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                InputStream requestBody = exchange.getRequestBody();
                String requestBodyString = new String(requestBody.readAllBytes());
                InputData inputData = new InputData(requestBodyString);

                // Risposta JSON
                String jsonResponse = "{"
                    + "\"city\":\"" + inputData.getCity() + "\","
                    + "\"start_date\":\"" + inputData.getInitialDate() + "\","
                    + "\"end_date\":\"" + inputData.getFinalDate() + "\""
                    + "}";

                System.out.println("Sending response: " + jsonResponse);

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes());
                os.close();
            }
        });


        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
            String uriPath = exchange.getRequestURI().getPath();
            if (uriPath == null || uriPath.equals("/")) {
                uriPath = "/index.html";
            }
            String relPath = uriPath.startsWith("/") ? uriPath.substring(1) : uriPath;

            Path base = Paths.get("Project/sds2/src/main/resources/static").toAbsolutePath().normalize();
            Path resolved = base.resolve(relPath).normalize();

            // Prevent path traversal and ensure file exists and isn't a directory
            if (!resolved.startsWith(base) || !Files.exists(resolved) || Files.isDirectory(resolved)) {
                String notFound = "404 (Not Found)\n";
                exchange.getResponseHeaders().add("Content-Type", "text/plain");
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound.getBytes());
                }
                return;
            }

            // Determine content type (probe + sensible fallbacks)
            String contentType = Files.probeContentType(resolved);
            if (contentType == null) {
                if (relPath.endsWith(".js")) contentType = "application/javascript";
                else if (relPath.endsWith(".css")) contentType = "text/css";
                else if (relPath.endsWith(".html")) contentType = "text/html";
                else if (relPath.endsWith(".json")) contentType = "application/json";
                else if (relPath.endsWith(".png")) contentType = "image/png";
                else if (relPath.endsWith(".jpg") || relPath.endsWith(".jpeg")) contentType = "image/jpeg";
                else contentType = "application/octet-stream";
            }

            byte[] bytes = Files.readAllBytes(resolved);
            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        });

        server.setExecutor(null); // crea un executor di default
        server.start();
        System.out.println("Server in esecuzione su http://localhost:" + port);
    }
}
