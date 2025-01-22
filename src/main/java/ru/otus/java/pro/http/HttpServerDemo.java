package ru.otus.java.pro.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class HttpServerDemo {
    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(HttpServerDemo.class.getClassLoader().getResourceAsStream("server.properties"));

            int port = Integer.parseInt(properties.getProperty("server.port"));
            int threadPoolSize = Integer.parseInt(properties.getProperty("server.threadPoolSize"));

            HttpServer server = new HttpServer(port, threadPoolSize);
            server.start();
        } catch (IOException e) {
            log.error("Error starting server", e);
        }
    }
}